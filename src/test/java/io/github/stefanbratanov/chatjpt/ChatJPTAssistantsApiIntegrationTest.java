package io.github.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class ChatJPTAssistantsApiIntegrationTest extends ChatJPTIntegrationTestBase {

  @Test
  public void testThreadsClient() {
    ThreadsClient threadsClient = chatJPT.threadsClient();

    ThreadMessage message =
        ThreadMessage.newBuilder()
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
}
