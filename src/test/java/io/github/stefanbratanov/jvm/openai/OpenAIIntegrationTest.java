package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.stefanbratanov.jvm.openai.ChatMessage.UserMessage.UserMessageWithContentParts.ContentPart.TextContentPart;
import java.io.UncheckedIOException;
import java.net.http.HttpTimeoutException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.*;

class OpenAIIntegrationTest extends OpenAIIntegrationTestBase {

  @Test
  void testUnauthorizedRequest() {
    OpenAI unauthorizedOpenAI = OpenAI.newBuilder("foobar").build();

    ModelsClient modelsClient = unauthorizedOpenAI.modelsClient();

    OpenAIException exception = assertThrows(OpenAIException.class, modelsClient::listModels);

    assertThat(exception.statusCode()).isEqualTo(401);
    assertThat(exception.errorMessage()).startsWith("Incorrect API key provided: foobar");

    // test capturing exception when stream requested
    CreateChatCompletionRequest streamRequest =
        CreateChatCompletionRequest.newBuilder()
            .message(ChatMessage.userMessage("Who won the world series in 2020?"))
            .stream(true)
            .build();

    ChatClient chatClient = unauthorizedOpenAI.chatClient();

    exception =
        assertThrows(OpenAIException.class, () -> chatClient.streamChatCompletion(streamRequest));

    assertThat(exception.statusCode()).isEqualTo(401);
    assertThat(exception.errorMessage()).startsWith("Incorrect API key provided: foobar");
  }

  @Test
  void testConfiguringRequestTimeout() {
    try (ClientAndServer mockServer = ClientAndServer.startClientAndServer()) {
      mockServer
          .when(HttpRequest.request())
          .respond(
              HttpResponse.response()
                  .withStatusCode(200)
                  .withBody(
                      "{\"id\":\"chatcmpl-123\",\"object\":\"chat.completion\",\"created\":1677652288,\"model\":\"gpt-3.5-turbo-0613\",\"system_fingerprint\":\"fp_44709d6fcb\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"Hello there, how may I assist you today?\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":9,\"completion_tokens\":12,\"total_tokens\":21}}")
                  // simulate a backend delay
                  .withDelay(Delay.seconds(1)));

      OpenAI openAI =
          OpenAI.newBuilder("foobar")
              .baseUrl("http://localhost:" + mockServer.getPort())
              // set the request timeout to less than the backend delay
              .requestTimeout(Duration.ofMillis(500))
              .build();

      ChatClient chatClient = openAI.chatClient();

      CreateChatCompletionRequest request =
          CreateChatCompletionRequest.newBuilder()
              .message(ChatMessage.userMessage("This is a timeout test"))
              .build();

      UncheckedIOException exception =
          assertThrows(UncheckedIOException.class, () -> chatClient.createChatCompletion(request));

      assertThat(exception).hasCauseInstanceOf(HttpTimeoutException.class);
    }
  }

  @Test
  void testChatClient() {
    ChatClient chatClient = openAI.chatClient();

    CreateChatCompletionRequest request =
        CreateChatCompletionRequest.newBuilder()
            .message(ChatMessage.userMessage("Who won the world series in 2020?"))
            .build();

    ChatCompletion completion = chatClient.createChatCompletion(request);

    assertThat(completion.choices())
        .hasSize(1)
        .first()
        .satisfies(choice -> assertThat(choice.message().content()).isNotNull());

    // test streaming
    CreateChatCompletionRequest streamRequest =
        CreateChatCompletionRequest.newBuilder()
            // test sending content part
            .message(ChatMessage.userMessage(new TextContentPart("Say this is a test")))
            .stream(true)
            .build();

    String joinedContent =
        chatClient
            .streamChatCompletion(streamRequest)
            .map(ChatCompletionChunk::choices)
            .map(
                choices -> {
                  assertThat(choices).hasSize(1);
                  return choices.get(0).delta().content();
                })
            .filter(Objects::nonNull)
            .collect(Collectors.joining());

    assertThat(joinedContent).containsPattern("(?i)this is (a|the) test");

    // test streaming with a subscriber
    CompletableFuture<String> joinedContentFuture = new CompletableFuture<>();
    chatClient.streamChatCompletion(
        streamRequest,
        new StreamChatCompletionSubscriber() {
          private final StringBuilder joinedContent = new StringBuilder();

          @Override
          public void onChunk(ChatCompletionChunk chunk) {
            List<ChatCompletionChunk.Choice> choices = chunk.choices();
            assertThat(choices).hasSize(1);
            String content = choices.get(0).delta().content();
            if (content != null) {
              joinedContent.append(content);
            }
          }

          @Override
          public void onException(Throwable ex) {
            Assertions.fail(ex);
          }

          @Override
          public void onComplete() {
            joinedContentFuture.complete(joinedContent.toString());
          }
        });

    assertThat(joinedContentFuture)
        .succeedsWithin(Duration.ofSeconds(30))
        .asString()
        .containsPattern("(?i)this is (a|the) test");
  }

  @Test
  void testModelsClient() {
    ModelsClient modelsClient = openAI.modelsClient();

    List<Model> models = modelsClient.listModels();

    assertThat(models).isNotEmpty();

    Model model = modelsClient.retrieveModel("gpt-3.5-turbo-instruct");

    assertThat(model).isNotNull();
  }

  @Test
  void testAudioClient(@TempDir Path tempDir) {
    AudioClient audioClient = openAI.audioClient();

    SpeechRequest speechRequest =
        SpeechRequest.newBuilder()
            .model("tts-1")
            .input("The quick brown fox jumped over the lazy dog.")
            .voice("alloy")
            .build();

    Path speech = tempDir.resolve("speech.mp3");

    // test async
    assertThat(audioClient.createSpeechAsync(speechRequest, speech))
        .succeedsWithin(Duration.ofMinutes(1));

    assertThat(speech).exists().isNotEmptyFile();

    TranscriptionRequest transcriptionRequest =
        TranscriptionRequest.newBuilder()
            .file(speech)
            .model("whisper-1")
            .responseFormat("text")
            .build();

    String transcript = audioClient.createTranscript(transcriptionRequest);

    assertThat(transcript)
        .isEqualToIgnoringNewLines("The quick brown fox jumped over the lazy dog.");

    Path greeting = getTestResource("/italian-greeting.mp3");

    TranslationRequest translationRequest =
        TranslationRequest.newBuilder()
            .file(greeting)
            .model("whisper-1")
            .responseFormat("json")
            .build();

    String translation = audioClient.createTranslation(translationRequest);

    assertThat(translation)
        .isEqualToIgnoringWhitespace("{\"text\":\"My name is Diego. What's your name?\"}");
  }

  @Test // using mock server because image models are costly
  void testImagesClient() {
    ImagesClient imagesClient = openAIWithMockServer.imagesClient();

    CreateImageRequest createImageRequest =
        CreateImageRequest.newBuilder()
            .prompt("Create a duck dressed up as superman")
            .responseFormat("b64_json")
            .build();

    Images createdImage = imagesClient.createImage(createImageRequest);

    assertThat(createdImage.data()).isNotEmpty().allMatch(Objects::nonNull);

    Path duck = getTestResource("/duck.png");

    EditImageRequest editImageRequest =
        EditImageRequest.newBuilder().image(duck).prompt("Make the duck swim in water").build();

    Images editedImage = imagesClient.editImage(editImageRequest);

    assertThat(editedImage.data()).isNotEmpty().allMatch(Objects::nonNull);

    Path duckSuperman = getTestResource("/duck-superman.png");

    CreateImageVariationRequest createImageVariationRequest =
        CreateImageVariationRequest.newBuilder().image(duckSuperman).n(2).build();

    // test async
    CompletableFuture<Images> imageVariationsFuture =
        imagesClient.createImageVariationAsync(createImageVariationRequest);

    assertThat(imageVariationsFuture)
        .succeedsWithin(Duration.ofMinutes(1))
        .satisfies(
            imageVariations ->
                assertThat(imageVariations.data()).isNotEmpty().allMatch(Objects::nonNull));
  }

  @Test
  void testModerationsClient() {
    ModerationsClient moderationsClient = openAI.moderationsClient();

    ModerationRequest request =
        ModerationRequest.newBuilder().input("I want to kill them.").build();

    Moderation moderation = moderationsClient.createModeration(request);

    assertThat(moderation.results())
        .hasSize(1)
        .allSatisfy(result -> assertThat(result.flagged()).isTrue());
  }

  @Test
  void testEmbeddingsClient() {
    EmbeddingsClient embeddingsClient = openAI.embeddingsClient();

    EmbeddingsRequest request =
        EmbeddingsRequest.newBuilder()
            .input("The food was delicious and the waiter...")
            .model("text-embedding-ada-002")
            .build();

    Embeddings embeddings = embeddingsClient.createEmbeddings(request);

    assertThat(embeddings.data())
        .hasSize(1)
        .allSatisfy(embedding -> assertThat(embedding.embedding()).isNotEmpty());
  }

  @Test
  void testFilesClient() {
    FilesClient filesClient = openAI.filesClient();

    Path jsonlFile = getTestResource("/mydata.jsonl");

    UploadFileRequest uploadFileRequest =
        UploadFileRequest.newBuilder().file(jsonlFile).purpose("fine-tune").build();

    File uploadedFile = filesClient.uploadFile(uploadFileRequest);

    List<File> uploadedFiles = filesClient.listFiles();

    assertThat(uploadedFiles).contains(uploadedFile);

    File retrievedFile = filesClient.retrieveFile(uploadedFile.id());

    assertThat(retrievedFile).isEqualTo(uploadedFile);
  }

  @Test // using mock server because fine-tuning models are costly
  void testFineTuningClient() {
    FineTuningClient fineTuningClient = openAIWithMockServer.fineTuningClient();

    CreateFineTuningJobRequest createFineTuningJobRequest =
        CreateFineTuningJobRequest.newBuilder()
            .trainingFile("123abc")
            .model("gpt-3.5-turbo")
            .build();

    FineTuningJob createdFineTuningJob =
        fineTuningClient.createFineTuningJob(createFineTuningJobRequest);

    assertThat(createdFineTuningJob).isNotNull();

    FineTuningClient.PaginatedFineTuningJobs fineTuningJobs =
        fineTuningClient.listFineTuningJobs(Optional.empty(), Optional.empty());

    assertThat(fineTuningJobs.hasMore()).isFalse();
    assertThat(fineTuningJobs.data())
        .anySatisfy(
            fineTuningJob -> assertThat(fineTuningJob.id()).isEqualTo(createdFineTuningJob.id()));

    FineTuningClient.PaginatedFineTuningEvents fineTuningJobEvents =
        fineTuningClient.listFineTuningJobEvents(
            createdFineTuningJob.id(), Optional.empty(), Optional.empty());

    assertThat(fineTuningJobEvents.hasMore()).isFalse();
    assertThat(fineTuningJobEvents.data()).isNotEmpty();

    FineTuningJob retrievedFineTuningJob =
        fineTuningClient.retrieveFineTuningJob(createdFineTuningJob.id());

    assertThat(retrievedFineTuningJob).isNotNull();

    FineTuningJob cancelledFineTuningJob =
        fineTuningClient.cancelFineTuningJob(createdFineTuningJob.id());

    assertThat(cancelledFineTuningJob).isNotNull();
  }
}
