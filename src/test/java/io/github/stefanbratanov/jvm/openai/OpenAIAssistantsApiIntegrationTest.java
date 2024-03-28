package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
        messagesClient.listMessages(thread.id(), PaginationQueryParameters.none());

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

    Assertions.assertThat(messageFiles).hasSize(1);

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

    String[] runFieldsToIgnore = new String[] {"status", "startedAt", "completedAt", "expiresAt"};

    assertThat(retrievedRun)
        .usingRecursiveComparison()
        .ignoringFields(runFieldsToIgnore)
        .isEqualTo(run);

    // create thread and run in one request
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
            .build();

    ThreadRun runWithThread = runsClient.createThreadAndRun(createThreadAndRunRequest);

    assertThat(runWithThread.assistantId()).isEqualTo(assistant.id());
    assertThat(runWithThread.threadId()).isNotNull();

    // retrieve runs
    List<ThreadRun> runs = runsClient.listRuns(threadId, PaginationQueryParameters.none()).data();

    assertThat(runs)
        .hasSize(1)
        .first()
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

    // retrieve run steps
    List<ThreadRunStep> runSteps =
        runsClient.listRunSteps(threadId, runId, PaginationQueryParameters.none()).data();

    assertThat(runSteps).isNotEmpty();

    ThreadRunStep runStep = runSteps.get(0);

    ThreadRunStep retrievedRunStep = runsClient.retrieveRunStep(threadId, runId, runStep.id());

    assertThat(retrievedRunStep).isEqualTo(runStep);

    // modify run
    ThreadRun modifiedRun =
        runsClient.modifyRun(
            threadId, runId, ModifyRunRequest.newBuilder().metadata(METADATA).build());

    assertThat(modifiedRun.metadata()).isEqualTo(METADATA);

    SubmitToolOutputsRequest submitToolOutputsRequest =
        new SubmitToolOutputsRequest(
            List.of(
                SubmitToolOutputsRequest.ToolOutput.newBuilder()
                    .toolCallId("call_abc123")
                    .output("28C")
                    .build()));

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
    threadsClient.deleteThread(runWithThread.threadId());
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
}
