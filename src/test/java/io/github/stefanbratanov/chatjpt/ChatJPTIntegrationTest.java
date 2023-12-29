package io.github.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ChatJPTIntegrationTest {

  private ChatJPT chatJPT;

  @BeforeEach
  public void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    chatJPT = ChatJPT.newBuilder(apiKey).build();
  }

  @Test
  public void testChatClient() {
    ChatClient chatClient = chatJPT.chatClient();

    ChatRequest request =
        ChatRequest.newBuilder()
            .message(Message.userMessage("Who won the world series in 2020?"))
            .build();

    ChatResponse response = chatClient.sendRequest(request);

    assertThat(response.choices())
        .hasSize(1)
        .first()
        .satisfies(choice -> assertThat(choice.message().content()).isNotNull());
  }

  @Test
  public void testModelsClient() {
    ModelsClient modelsClient = chatJPT.modelsClient();

    List<Model> models = modelsClient.getModels();

    assertThat(models).isNotEmpty();

    Model model = modelsClient.getModel("gpt-3.5-turbo-instruct");

    assertThat(model).isNotNull();
  }

  @Test
  public void testAudioClient(@TempDir Path tempDir) {
    AudioClient audioClient = chatJPT.audioClient();

    SpeechRequest speechRequest =
        SpeechRequest.newBuilder()
            .model("tts-1")
            .input("You're gonna eat lightning, you're gonna crap thunder!")
            .voice("alloy")
            .build();

    Path speech = tempDir.resolve("rocky.mp3");

    audioClient.createSpeech(speechRequest, speech);

    assertThat(speech).exists().isNotEmptyFile();

    TranscriptionRequest transcriptionRequest =
        TranscriptionRequest.newBuilder().file(speech).model("whisper-1").build();

    String transcript = audioClient.createTranscript(transcriptionRequest);

    assertThat(transcript).isNotEmpty();
  }
}
