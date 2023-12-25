package com.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatJPTIntegrationTest {

  private ChatJPT chatJPT;

  @BeforeEach
  public void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    chatJPT = ChatJPT.newBuilder(apiKey).build();
  }

  @Test
  public void testChatClient() {
    ChatClient chatClient = chatJPT.newChatClient();

    ChatRequest request =
        ChatRequest.newBuilder()
            .message(Message.userMessage("Who won the world series in 2020?"))
            .build();

    ChatResponse response = chatClient.sendRequest(request);

    assertThat(response.message().content()).isNotNull();
  }

  @Test
  public void testModelsClient() {
    ModelsClient modelsClient = chatJPT.newModelsClient();

    List<Model> models = modelsClient.getModels();

    assertThat(models).isNotEmpty();

    Model model = modelsClient.getModel("gpt-3.5-turbo-instruct");

    assertThat(model).isNotNull();
  }
}
