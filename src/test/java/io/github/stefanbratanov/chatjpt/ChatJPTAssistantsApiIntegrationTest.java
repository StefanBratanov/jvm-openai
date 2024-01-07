package io.github.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ChatJPTAssistantsApiIntegrationTest extends ChatJPTIntegrationTestBase {

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
    FilesClient filesClient = chatJPT.filesClient();
    ThreadsClient threadsClient = chatJPT.threadsClient();

    MessagesClient messagesClient = chatJPT.messagesClient();

    // create file
    Path jsonlFile = getTestResource("/mydata.jsonl");
    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder().file(jsonlFile).purpose("fine-tune").build();
    File file = filesClient.uploadFile(uploadFileRequest);
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

    PaginationQueryParameters emptyQueryParameters = PaginationQueryParameters.newBuilder().build();

    MessagesClient.PaginatedThreadMessages paginatedMessages =
        messagesClient.listMessages(thread.id(), emptyQueryParameters);

    assertThat(paginatedMessages.hasMore()).isFalse();
    assertThat(paginatedMessages.firstId()).isEqualTo(createdMessage.id());
    assertThat(paginatedMessages.lastId()).isEqualTo(createdMessage.id());

    List<ThreadMessage> messages = paginatedMessages.data();

    assertThat(messages)
        .hasSize(1)
        .first()
        .satisfies(message -> assertThat(message).isEqualTo(createdMessage));

    MessagesClient.PaginatedThreadMessageFiles paginatedMessageFiles =
        messagesClient.listMessageFiles(thread.id(), createdMessage.id(), emptyQueryParameters);

    assertThat(paginatedMessageFiles.hasMore()).isFalse();

    List<ThreadMessageFile> messageFiles = paginatedMessageFiles.data();

    assertThat(messageFiles).hasSize(1);

    ThreadMessageFile messageFile = messageFiles.get(0);

    ThreadMessageFile retrievedMessageFile =
        messagesClient.retrieveMessageFile(thread.id(), createdMessage.id(), messageFile.id());

    assertThat(messageFile).isEqualTo(retrievedMessageFile);

    Map<String, String> metadata = Map.of("modified", "true", "user", "abc123");

    ThreadMessage modifiedMessage =
        messagesClient.modifyMessage(
            thread.id(),
            createdMessage.id(),
            ModifyMessageRequest.newBuilder().metadata(metadata).build());

    assertThat(modifiedMessage.metadata()).isEqualTo(metadata);
  }
}
