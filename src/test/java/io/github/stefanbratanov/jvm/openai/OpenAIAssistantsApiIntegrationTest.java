package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*\\S.*")
class OpenAIAssistantsApiIntegrationTest extends OpenAIIntegrationTestBase {

  private static final Map<String, String> METADATA = Map.of("modified", "true", "user", "abc123");

  @Test
  void testThreadsClient() {
    ThreadsClient threadsClient = openAI.threadsClient();

    CreateThreadRequest.Message message =
        CreateThreadRequest.Message.newBuilder()
            .content("I need to solve the equation `3x + 11 = 14`. Can you help me?")
            .build();
    CreateThreadRequest createRequest = CreateThreadRequest.newBuilder().message(message).build();

    Thread createdThread = threadsClient.createThread(createRequest);

    Thread retrievedThread = threadsClient.retrieveThread(createdThread.id());

    assertThat(createdThread).isEqualTo(retrievedThread);

    ModifyThreadRequest modifyRequest = ModifyThreadRequest.newBuilder().metadata(METADATA).build();

    Thread modifiedThread = threadsClient.modifyThread(createdThread.id(), modifyRequest);

    assertThat(modifiedThread.metadata()).isEqualTo(METADATA);

    DeletionStatus deletionStatus = threadsClient.deleteThread(createdThread.id());

    assertThat(deletionStatus.id()).isEqualTo(createdThread.id());
    assertThat(deletionStatus.deleted()).isTrue();
  }

  @Test
  void testMessagesClient() {
    ThreadsClient threadsClient = openAI.threadsClient();

    MessagesClient messagesClient = openAI.messagesClient();

    // upload file
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder()
            .file(getTestResource("/mydata.jsonl"))
            .purpose("fine-tune")
            .build();
    File file = openAI.filesClient().uploadFile(uploadFileRequest);
    // create thread
    Thread thread = threadsClient.createThread(CreateThreadRequest.newBuilder().build());

    CreateMessageRequest createRequest =
        CreateMessageRequest.newBuilder()
            .content("How does AI work? Explain it in simple terms.")
            .fileIds(List.of(file.id()))
            .build();

    ThreadMessage createdMessage = messagesClient.createMessage(thread.id(), createRequest);

    assertThat(createdMessage).isNotNull();

    ThreadMessage retrievedMessage =
        messagesClient.retrieveMessage(thread.id(), createdMessage.id());

    assertThat(retrievedMessage).isEqualTo(createdMessage);

    MessagesClient.PaginatedThreadMessages paginatedMessages =
        messagesClient.listMessages(
            thread.id(), PaginationQueryParameters.none(), Optional.empty());

    assertThat(paginatedMessages.hasMore()).isFalse();
    assertThat(paginatedMessages.firstId()).isEqualTo(createdMessage.id());
    assertThat(paginatedMessages.lastId()).isEqualTo(createdMessage.id());

    List<ThreadMessage> messages = paginatedMessages.data();

    assertThat(messages)
        .hasSize(1)
        .first()
        .satisfies(message -> assertThat(message).isEqualTo(createdMessage));

    MessagesClient.PaginatedThreadMessageFiles paginatedMessageFiles =
        messagesClient.listMessageFiles(
            thread.id(), createdMessage.id(), PaginationQueryParameters.none());

    assertThat(paginatedMessageFiles.hasMore()).isFalse();

    List<ThreadMessageFile> messageFiles = paginatedMessageFiles.data();

    assertThat(messageFiles).hasSize(1);

    ThreadMessageFile messageFile = messageFiles.get(0);

    ThreadMessageFile retrievedMessageFile =
        messagesClient.retrieveMessageFile(thread.id(), createdMessage.id(), messageFile.id());

    assertThat(retrievedMessageFile).isEqualTo(messageFile);

    ThreadMessage modifiedMessage =
        messagesClient.modifyMessage(
            thread.id(),
            createdMessage.id(),
            ModifyMessageRequest.newBuilder().metadata(METADATA).build());

    assertThat(modifiedMessage.metadata()).isEqualTo(METADATA);
  }

  @Test
  void testAssistantsClient() {
    AssistantsClient assistantsClient = openAI.assistantsClient();

    File file = uploadRealEstateAgentAssistantFile();

    CreateAssistantRequest createRequest =
        CreateAssistantRequest.newBuilder()
            .name("Test assistant")
            .model("gpt-3.5-turbo-1106")
            .instructions(
                "You are a real estate agent bot, who has access to the house the user is willing to buy.")
            .tool(Tool.retrievalTool())
            .build();

    Assistant createdAssistant = assistantsClient.createAssistant(createRequest);

    assertThat(createdAssistant).isNotNull();

    AssistantFile createdAssistantFile =
        assistantsClient.createAssistantFile(createdAssistant.id(), file.id());

    assertThat(createdAssistantFile).isNotNull();

    AssistantFile retrievedAssistantFile =
        assistantsClient.retrieveAssistantFile(createdAssistant.id(), createdAssistantFile.id());

    assertThat(retrievedAssistantFile).isEqualTo(createdAssistantFile);

    Assistant retrievedAssistant = assistantsClient.retrieveAssistant(createdAssistant.id());

    assertThat(retrievedAssistant)
        .usingRecursiveComparison()
        .ignoringFields("fileIds")
        .isEqualTo(createdAssistant);

    AssistantsClient.PaginatedAssistants assistants =
        assistantsClient.listAssistants(PaginationQueryParameters.none());

    assertThat(assistants.data()).contains(retrievedAssistant);

    AssistantsClient.PaginatedAssistantFiles assistantFiles =
        assistantsClient.listAssistantFiles(
            createdAssistant.id(), PaginationQueryParameters.none());

    assertThat(assistantFiles.data()).contains(retrievedAssistantFile);

    Assistant modifiedAssistant =
        assistantsClient.modifyAssistant(
            createdAssistant.id(), ModifyAssistantRequest.newBuilder().metadata(METADATA).build());

    assertThat(modifiedAssistant)
        .usingRecursiveComparison()
        .ignoringFields("metadata")
        .isEqualTo(retrievedAssistant);

    assertThat(modifiedAssistant.metadata()).isEqualTo(METADATA);

    // cleanup
    DeletionStatus deletionStatus =
        assistantsClient.deleteAssistantFile(createdAssistant.id(), createdAssistantFile.id());
    assertThat(deletionStatus.deleted()).isTrue();
    deletionStatus = assistantsClient.deleteAssistant(createdAssistant.id());
    assertThat(deletionStatus.deleted()).isTrue();
  }

  @Test
  void testRunsClient() {
    ThreadsClient threadsClient = openAI.threadsClient();
    AssistantsClient assistantsClient = openAI.assistantsClient();

    RunsClient runsClient = openAI.runsClient();

    // create thread
    CreateThreadRequest createThreadRequest =
        CreateThreadRequest.newBuilder()
            .message(
                CreateThreadRequest.Message.newBuilder().content("What is the house size?").build())
            .build();

    Thread thread = threadsClient.createThread(createThreadRequest);
    String threadId = thread.id();

    // create assistant
    File assistantFile = uploadRealEstateAgentAssistantFile();

    CreateAssistantRequest createAssistantRequest =
        CreateAssistantRequest.newBuilder()
            .name("Test assistant")
            .model("gpt-3.5-turbo-1106")
            .instructions(
                "You are a real estate agent bot, who has access to the house the user is willing to buy.")
            .fileIds(List.of(assistantFile.id()))
            .tool(Tool.retrievalTool())
            .build();

    Assistant assistant = assistantsClient.createAssistant(createAssistantRequest);

    // create run
    CreateRunRequest createRequest =
        CreateRunRequest.newBuilder().assistantId(assistant.id()).build();

    ThreadRun run = runsClient.createRun(threadId, createRequest);
    String runId = run.id();

    assertThat(run.threadId()).isEqualTo(threadId);
    assertThat(run.assistantId()).isEqualTo(assistant.id());

    ThreadRun retrievedRun = runsClient.retrieveRun(threadId, runId);

    String[] runFieldsToIgnore =
        new String[] {"status", "startedAt", "completedAt", "expiresAt", "usage"};

    assertThat(retrievedRun)
        .usingRecursiveComparison()
        .ignoringFields(runFieldsToIgnore)
        .isEqualTo(run);

    // wait for the run to complete, fail or expire
    awaitCondition(
        () -> {
          String status = runsClient.retrieveRun(threadId, runId).status();
          return status.equals("completed") || status.equals("failed") || status.equals("expired");
        },
        Duration.ofSeconds(5),
        Duration.ofMinutes(1));

    // create thread and run in one request and test streaming with a subscriber
    CreateThreadAndRunRequest createThreadAndRunRequest =
        CreateThreadAndRunRequest.newBuilder()
            .assistantId(assistant.id())
            .thread(
                CreateThreadRequest.newBuilder()
                    .messages(
                        List.of(
                            CreateThreadRequest.Message.newBuilder()
                                .content("What is the house address?")
                                .build()))
                    .build())
            .stream(true)
            .build();

    // capture all types of events
    CompletableFuture<Set<String>> emittedEventsFuture = new CompletableFuture<>();
    CompletableFuture<String> threadIdToDeleteFuture = new CompletableFuture<>();

    runsClient.createThreadAndRunAndStream(
        createThreadAndRunRequest,
        new AssistantStreamEventSubscriber() {
          private final Set<String> emittedEvents = new LinkedHashSet<>();

          @Override
          public void onThread(String event, Thread thread) {
            assertEventStartsWithAndDataIsNotNull(event, "thread", thread);
            emittedEvents.add(event);
            threadIdToDeleteFuture.complete(thread.id());
          }

          @Override
          public void onThreadRun(String event, ThreadRun threadRun) {
            assertEventStartsWithAndDataIsNotNull(event, "thread.run", threadRun);
            emittedEvents.add(event);
          }

          @Override
          public void onThreadRunStep(String event, ThreadRunStep threadRunStep) {
            assertEventStartsWithAndDataIsNotNull(event, "thread.run.step", threadRunStep);
            emittedEvents.add(event);
          }

          @Override
          public void onThreadRunStepDelta(String event, ThreadRunStepDelta threadRunStepDelta) {
            assertEventStartsWithAndDataIsNotNull(
                event, "thread.run.step.delta", threadRunStepDelta);
            emittedEvents.add(event);
          }

          @Override
          public void onThreadMessage(String event, ThreadMessage threadMessage) {
            assertEventStartsWithAndDataIsNotNull(event, "thread.message", threadMessage);
            emittedEvents.add(event);
          }

          @Override
          public void onThreadMessageDelta(String event, ThreadMessageDelta threadMessageDelta) {
            assertEventStartsWithAndDataIsNotNull(
                event, "thread.message.delta", threadMessageDelta);
            emittedEvents.add(event);
          }

          @Override
          public void onUnknownEvent(String event, String data) {
            String message =
                String.format("Unknown event %s and data %s have been received", event, data);
            Assertions.fail(message);
          }

          @Override
          public void onException(Throwable ex) {
            Assertions.fail(ex);
          }

          @Override
          public void onComplete() {
            emittedEventsFuture.complete(emittedEvents);
          }
        });

    assertThat(emittedEventsFuture)
        .succeedsWithin(Duration.ofMinutes(1))
        .satisfies(
            emittedEvents ->
                assertThat(emittedEvents)
                    .containsExactly(
                        "thread.created",
                        "thread.run.created",
                        "thread.run.queued",
                        "thread.run.in_progress",
                        "thread.run.step.created",
                        "thread.run.step.in_progress",
                        "thread.run.step.delta",
                        "thread.run.step.completed",
                        "thread.message.created",
                        "thread.message.in_progress",
                        "thread.message.delta",
                        "thread.message.completed",
                        "thread.run.completed"));

    assertThat(threadIdToDeleteFuture)
        .isCompletedWithValueMatching(
            threadIdToDelete -> threadsClient.deleteThread(threadIdToDelete).deleted());

    // retrieve runs
    List<ThreadRun> runs = runsClient.listRuns(threadId, PaginationQueryParameters.none()).data();

    assertThat(runs)
        .hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(runFieldsToIgnore)
        .isEqualTo(run);

    // retrieve run steps
    List<ThreadRunStep> runSteps =
        runsClient.listRunSteps(threadId, runId, PaginationQueryParameters.none()).data();

    assertThat(runSteps)
        .first()
        .satisfies(
            runStep ->
                assertThat(runStep)
                    .isEqualTo(runsClient.retrieveRunStep(threadId, runId, runStep.id())));

    // modify run
    ThreadRun modifiedRun =
        runsClient.modifyRun(
            threadId, runId, ModifyRunRequest.newBuilder().metadata(METADATA).build());

    assertThat(modifiedRun.metadata()).isEqualTo(METADATA);

    SubmitToolOutputsRequest submitToolOutputsRequest =
        SubmitToolOutputsRequest.newBuilder()
            .toolOutputs(
                List.of(
                    SubmitToolOutputsRequest.ToolOutput.newBuilder()
                        .toolCallId("call_abc123")
                        .output("28C")
                        .build()))
            .build();

    OpenAIException submitToolOutputException =
        assertThrows(
            OpenAIException.class,
            () -> runsClient.submitToolOutputs(threadId, runId, submitToolOutputsRequest));

    assertThat(submitToolOutputException.statusCode()).isEqualTo(400);
    assertThat(submitToolOutputException.errorMessage()).contains("do not accept tool outputs");

    OpenAIException cancelRunException =
        assertThrows(OpenAIException.class, () -> runsClient.cancelRun(threadId, runId));

    assertThat(cancelRunException.statusCode()).isEqualTo(400);
    assertThat(cancelRunException.errorMessage()).contains("Cannot cancel run");

    // cleanup
    threadsClient.deleteThread(threadId);
    assistantsClient.deleteAssistantFile(assistant.id(), assistantFile.id());
    assistantsClient.deleteAssistant(assistant.id());
  }

  private File uploadRealEstateAgentAssistantFile() {
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder()
            .file(getTestResource("/real-estate-agent-assistant.txt"))
            .purpose("assistants")
            .build();
    return openAI.filesClient().uploadFile(uploadFileRequest);
  }

  private <T> void assertEventStartsWithAndDataIsNotNull(String event, String prefix, T data) {
    assertThat(event).startsWith(prefix);
    assertThat(data).isNotNull();
  }
}
