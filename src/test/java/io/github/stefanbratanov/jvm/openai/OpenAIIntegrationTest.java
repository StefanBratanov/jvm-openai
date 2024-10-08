package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.stefanbratanov.jvm.openai.ContentPart.TextContentPart;
import io.github.stefanbratanov.jvm.openai.CreateChatCompletionRequest.StreamOptions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.*;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*\\S.*")
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
            // test usage stats
            .streamOptions(StreamOptions.withUsageIncluded())
            .build();

    String joinedContent =
        chatClient
            .streamChatCompletion(streamRequest)
            .filter(
                chunk -> {
                  if (chunk.choices().isEmpty()) {
                    assertThat(chunk.usage()).isNotNull();
                    return false;
                  }
                  return true;
                })
            .map(ChatCompletionChunk::choices)
            .map(
                choices -> {
                  assertThat(choices).hasSize(1);
                  return choices.get(0).delta().content();
                })
            .filter(Objects::nonNull)
            .collect(Collectors.joining());

    assertThat(joinedContent).containsPattern("(?i)this is (a|the) test");

    streamRequest =
        CreateChatCompletionRequest.newBuilder()
            .message(ChatMessage.userMessage("Say this is a test"))
            .stream(true)
            .build();

    // test streaming with a subscriber
    CompletableFuture<String> joinedContentFuture = new CompletableFuture<>();
    chatClient.streamChatCompletion(
        streamRequest,
        new ChatCompletionStreamSubscriber() {
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
            .model(OpenAIModel.TTS_1)
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
            .model(OpenAIModel.WHISPER_1)
            .responseFormat("text")
            .build();

    String transcript = audioClient.createTranscript(transcriptionRequest);

    assertThat(transcript)
        .isEqualToIgnoringNewLines("The quick brown fox jumped over the lazy dog.");

    Path greeting = getTestResource("/italian-greeting.mp3");

    TranslationRequest translationRequest =
        TranslationRequest.newBuilder()
            .file(greeting)
            .model(OpenAIModel.WHISPER_1)
            .responseFormat(AudioResponseFormat.JSON)
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
  void testBatchClient() {
    FilesClient filesClient = openAI.filesClient();

    UploadFileRequest uploadInputFileRequest =
        UploadFileRequest.newBuilder()
            .file(getTestResource("/batch-input-file.jsonl"))
            .purpose(Purpose.BATCH)
            .build();

    File inputFile = filesClient.uploadFile(uploadInputFileRequest);

    BatchClient batchClient = openAI.batchClient();

    CreateBatchRequest request =
        CreateBatchRequest.newBuilder()
            .inputFileId(inputFile.id())
            .endpoint("/v1/chat/completions")
            .completionWindow("24h")
            .build();

    Batch batch = batchClient.createBatch(request);

    assertThat(batch.inputFileId()).isEqualTo(inputFile.id());
    assertThat(batch.errors()).isNull();

    BatchClient.PaginatedBatches paginatedBatches =
        batchClient.listBatches(Optional.empty(), Optional.empty());

    assertThat(paginatedBatches.data()).isNotEmpty();
    assertThat(paginatedBatches.firstId()).isNotNull();
    assertThat(paginatedBatches.lastId()).isNotNull();
    // assert that the batch we just created is listed
    assertThat(paginatedBatches.data())
        .anySatisfy(listedBatch -> assertThat(listedBatch.id()).isEqualTo(batch.id()));

    // immediately cancel the batch, because can't wait for batch to finish in tests
    Batch cancelledBatch = batchClient.cancelBatch(batch.id());

    assertThat(cancelledBatch.id()).isEqualTo(batch.id());
    assertThat(cancelledBatch.cancellingAt()).isNotNull();

    // test retrieving
    Batch retrievedBatch = batchClient.retrieveBatch(batch.id());

    assertThat(retrievedBatch.id()).isEqualTo(batch.id());
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
        UploadFileRequest.newBuilder().file(jsonlFile).purpose(Purpose.FINE_TUNE).build();

    File uploadedFile = filesClient.uploadFile(uploadFileRequest);

    List<File> uploadedFiles = filesClient.listFiles();

    assertThat(uploadedFiles).contains(uploadedFile);

    File retrievedFile = filesClient.retrieveFile(uploadedFile.id());

    assertThat(retrievedFile).isEqualTo(uploadedFile);
  }

  @Test
  void testUploadsClient(@TempDir Path tempDir) throws IOException {
    UploadsClient uploadsClient = openAI.uploadsClient();
    FilesClient filesClient = openAI.filesClient();

    CreateUploadRequest createUploadRequest =
        CreateUploadRequest.newBuilder()
            .filename("hello.txt")
            .purpose(Purpose.BATCH)
            .bytes(11)
            .mimeType("text/plain")
            .build();

    Upload upload = uploadsClient.createUpload(createUploadRequest);

    Path part1 = tempDir.resolve("part1.txt");
    Path part2 = tempDir.resolve("part2.txt");

    Files.writeString(part1, "Hello ");
    Files.writeString(part2, "World");

    UploadPart uploadPart = uploadsClient.addUploadPart(upload.id(), part1);
    UploadPart uploadPart2 = uploadsClient.addUploadPart(upload.id(), part2);

    CompleteUploadRequest completeUploadRequest =
        CompleteUploadRequest.newBuilder()
            .partIds(List.of(uploadPart.id(), uploadPart2.id()))
            .build();

    Upload completedUpload = uploadsClient.completeUpload(upload.id(), completeUploadRequest);

    assertThat(completedUpload.status()).isEqualTo("completed");

    File file = completedUpload.file();

    assertThat(file).isNotNull();

    byte[] retrievedContent = filesClient.retrieveFileContent(file.id());

    assertThat(new String(retrievedContent)).isEqualTo("Hello World");
  }

  @Test // using mock server because fine-tuning models are costly
  void testFineTuningClient() {
    FineTuningClient fineTuningClient = openAIWithMockServer.fineTuningClient();

    CreateFineTuningJobRequest createFineTuningJobRequest =
        CreateFineTuningJobRequest.newBuilder()
            .trainingFile("123abc")
            .model(OpenAIModel.GPT_3_5_TURBO)
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

    FineTuningClient.PaginatedFineTuningCheckpoints fineTuningCheckpoints =
        fineTuningClient.listFineTuningCheckpoints(
            createdFineTuningJob.id(), Optional.empty(), Optional.empty());

    assertThat(fineTuningCheckpoints.hasMore()).isFalse();
    assertThat(fineTuningCheckpoints.data()).isNotEmpty();

    FineTuningJob retrievedFineTuningJob =
        fineTuningClient.retrieveFineTuningJob(createdFineTuningJob.id());

    assertThat(retrievedFineTuningJob).isNotNull();

    FineTuningJob cancelledFineTuningJob =
        fineTuningClient.cancelFineTuningJob(createdFineTuningJob.id());

    assertThat(cancelledFineTuningJob).isNotNull();
  }
}
