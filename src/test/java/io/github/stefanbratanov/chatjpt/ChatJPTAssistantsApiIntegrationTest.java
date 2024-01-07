package io.github.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ChatJPTAssistantsApiIntegrationTest extends ChatJPTIntegrationTestBase {

  private static final PaginationQueryParameters EMPTY_QUERY_PARAMETERS =
      PaginationQueryParameters.newBuilder().build();

  @Test
  public void testThreadsClient() {
    ThreadsClient threadsClient = chatJPT.threadsClient();

    CreateThreadRequest.Message message =
        CreateThreadRequest.Message.newBuilder()
            .content("I need to solve the equation `3x + 11 = 14`. Can you help me?")
            .build();
    CreateThreadRequest createRequest = CreateThreadRequest.newBuilder().message(message).build();

    Thread createdThread = threadsClient.createThread(createRequest);

    Thread retrievedThread = threadsClient.retrieveThread(createdThread.id());

    assertThat(createdThread).isEqualTo(retrievedThread);

    Map<String, String> metadata = Map.of("modified", "true", "user", "abc123");

    ModifyThreadRequest modifyRequest = ModifyThreadRequest.newBuilder().metadata(metadata).build();

    Thread modifiedThread = threadsClient.modifyThread(createdThread.id(), modifyRequest);

    assertThat(modifiedThread.metadata()).isEqualTo(metadata);

    DeletionStatus deletionStatus = threadsClient.deleteThread(createdThread.id());

    assertThat(deletionStatus.id()).isEqualTo(createdThread.id());
    assertThat(deletionStatus.deleted()).isTrue();
  }

  @Test
  public void testMessagesClient() {
    ThreadsClient threadsClient = chatJPT.threadsClient();

    MessagesClient messagesClient = chatJPT.messagesClient();

    // upload file
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder()
            .file(getTestResource("/mydata.jsonl"))
            .purpose("fine-tune")
            .build();
    File file = chatJPT.filesClient().uploadFile(uploadFileRequest);
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
        messagesClient.listMessages(thread.id(), EMPTY_QUERY_PARAMETERS);

    assertThat(paginatedMessages.hasMore()).isFalse();
    assertThat(paginatedMessages.firstId()).isEqualTo(createdMessage.id());
    assertThat(paginatedMessages.lastId()).isEqualTo(createdMessage.id());

    List<ThreadMessage> messages = paginatedMessages.data();

    assertThat(messages)
        .hasSize(1)
        .first()
        .satisfies(message -> assertThat(message).isEqualTo(createdMessage));

    MessagesClient.PaginatedThreadMessageFiles paginatedMessageFiles =
        messagesClient.listMessageFiles(thread.id(), createdMessage.id(), EMPTY_QUERY_PARAMETERS);

    assertThat(paginatedMessageFiles.hasMore()).isFalse();

    List<ThreadMessageFile> messageFiles = paginatedMessageFiles.data();

    assertThat(messageFiles).hasSize(1);

    ThreadMessageFile messageFile = messageFiles.get(0);

    ThreadMessageFile retrievedMessageFile =
        messagesClient.retrieveMessageFile(thread.id(), createdMessage.id(), messageFile.id());

    assertThat(retrievedMessageFile).isEqualTo(messageFile);

    Map<String, String> metadata = Map.of("modified", "true", "user", "abc123");

    ThreadMessage modifiedMessage =
        messagesClient.modifyMessage(
            thread.id(),
            createdMessage.id(),
            ModifyMessageRequest.newBuilder().metadata(metadata).build());

    assertThat(modifiedMessage.metadata()).isEqualTo(metadata);
  }

  @Test
  public void testAssistantsClient() {
    AssistantsClient assistantsClient = chatJPT.assistantsClient();

    // upload assistant file
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder()
            .file(getTestResource("/assistant.txt"))
            .purpose("assistants")
            .build();
    File file = chatJPT.filesClient().uploadFile(uploadFileRequest);

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
        assistantsClient.listAssistants(EMPTY_QUERY_PARAMETERS);

    assertThat(assistants.data()).contains(retrievedAssistant);

    AssistantsClient.PaginatedAssistantFiles assistantFiles =
        assistantsClient.listAssistantFiles(createdAssistant.id(), EMPTY_QUERY_PARAMETERS);

    assertThat(assistantFiles.data()).contains(retrievedAssistantFile);

    Map<String, String> metadata = Map.of("modified", "true", "user", "abc123");

    Assistant modifiedAssistant =
        assistantsClient.modifyAssistant(
            createdAssistant.id(), ModifyAssistantRequest.newBuilder().metadata(metadata).build());

    assertThat(modifiedAssistant)
        .usingRecursiveComparison()
        .ignoringFields("metadata")
        .isEqualTo(retrievedAssistant);

    assertThat(modifiedAssistant.metadata()).isEqualTo(metadata);

    // cleanup
    DeletionStatus deletionStatus =
        assistantsClient.deleteAssistantFile(createdAssistant.id(), createdAssistantFile.id());
    assertThat(deletionStatus.deleted()).isTrue();
    deletionStatus = assistantsClient.deleteAssistant(createdAssistant.id());
    assertThat(deletionStatus.deleted()).isTrue();
  }
}
