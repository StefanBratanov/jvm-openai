package io.github.stefanbratanov.jvm.openai;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Turn audio into text or text into audio.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/audio">Audio</a>
 */
public final class AudioClient extends OpenAIClient {

  private final URI baseUrl;

  AudioClient(URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    super(apiKey, organization, httpClient);
    this.baseUrl = baseUrl;
  }

  /**
   * Generates audio from the input text.
   *
   * @param output the file where to save the audio
   * @throws OpenAIException in case of API errors
   */
  public void createSpeech(SpeechRequest request, Path output) {
    createParentDirectories(output);
    HttpRequest httpRequest = createSpeechPostRequest(request);

    sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofFile(output));
  }

  /**
   * Same as {@link #createSpeech(SpeechRequest,Path)} but returns a response in a {@link
   * CompletableFuture}
   */
  public CompletableFuture<Void> createSpeechAsync(SpeechRequest request, Path output) {
    createParentDirectories(output);
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.SPEECH.getPath()))
            .POST(createBodyPublisher(request))
            .build();

    return sendHttpRequestAsync(httpRequest, HttpResponse.BodyHandlers.ofFile(output))
        .thenApply(httpResponse -> null);
  }

  /**
   * Transcribes audio into the input language.
   *
   * @throws OpenAIException in case of API errors
   */
  public String createTranscript(TranscriptionRequest request) {
    HttpRequest httpRequest = createTranscriptPostRequest(request);

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponseAsTree(httpResponse.body()).get("text").asText();
  }

  /**
   * Same as {@link #createTranscript(TranscriptionRequest)} but returns a response in a {@link
   * CompletableFuture}
   */
  public CompletableFuture<String> createTranscriptAsync(TranscriptionRequest request) {
    HttpRequest httpRequest = createTranscriptPostRequest(request);

    return sendHttpRequestAsync(httpRequest)
        .thenApply(
            httpResponse -> deserializeResponseAsTree(httpResponse.body()).get("text").asText());
  }

  /**
   * Translates audio into English.
   *
   * @throws OpenAIException in case of API errors
   */
  public String createTranslation(TranslationRequest request) {
    HttpRequest httpRequest = createTranslationPostRequest(request);

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponseAsTree(httpResponse.body()).get("text").asText();
  }

  /**
   * Same as {@link #createTranslation(TranslationRequest)} but returns a response in a {@link
   * CompletableFuture}
   */
  public CompletableFuture<String> createTranslationAsync(TranslationRequest request) {
    HttpRequest httpRequest = createTranslationPostRequest(request);

    return sendHttpRequestAsync(httpRequest)
        .thenApply(
            httpResponse -> deserializeResponseAsTree(httpResponse.body()).get("text").asText());
  }

  private void createParentDirectories(Path path) {
    try {
      Path parentPath = path.getParent();
      if (parentPath != null) {
        Files.createDirectories(parentPath);
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private HttpRequest createSpeechPostRequest(SpeechRequest request) {
    return newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
        .uri(baseUrl.resolve(Endpoint.SPEECH.getPath()))
        .POST(createBodyPublisher(request))
        .build();
  }

  private HttpRequest createTranscriptPostRequest(TranscriptionRequest request) {
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder()
            .filePart("file", request.file())
            .textPart("model", request.model());
    request
        .language()
        .ifPresent(language -> multipartBodyPublisherBuilder.textPart("language", language));
    request.prompt().ifPresent(prompt -> multipartBodyPublisherBuilder.textPart("prompt", prompt));
    request
        .temperature()
        .ifPresent(
            temperature -> multipartBodyPublisherBuilder.textPart("temperature", temperature));

    MultipartBodyPublisher multipartBodyPublisher = multipartBodyPublisherBuilder.build();

    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
        .uri(baseUrl.resolve(Endpoint.TRANSCRIPTION.getPath()))
        .POST(multipartBodyPublisher)
        .build();
  }

  private HttpRequest createTranslationPostRequest(TranslationRequest request) {
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder()
            .filePart("file", request.file())
            .textPart("model", request.model());
    request.prompt().ifPresent(prompt -> multipartBodyPublisherBuilder.textPart("prompt", prompt));
    request
        .temperature()
        .ifPresent(
            temperature -> multipartBodyPublisherBuilder.textPart("temperature", temperature));

    MultipartBodyPublisher multipartBodyPublisher = multipartBodyPublisherBuilder.build();

    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
        .uri(baseUrl.resolve(Endpoint.TRANSLATION.getPath()))
        .POST(multipartBodyPublisher)
        .build();
  }
}
