package io.github.stefanbratanov.chatjpt;

import org.junit.jupiter.api.BeforeEach;

public class ChatJPTIntegrationTestBase {

  protected ChatJPT chatJPT;

  @BeforeEach
  public void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    chatJPT = ChatJPT.newBuilder(apiKey).build();
  }
}
