package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;

/** Based on <a href="https://platform.openai.com/docs/api-reference/audio">Audio</a> */
public final class AudioClient extends OpenAIClient {

  private final URI baseUrl;

  AudioClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    this.baseUrl = baseUrl;
  }

  /**
   * Generates audio from the input text.
   *
   * @param output the file where to save the audio
   * @throws OpenAIException in case of API errors
   */
  public void createSpeech(SpeechRequest request, Path output) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.SPEECH.getPath()))
            .POST(createBodyPublisher(request))
            .build();

    sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofFile(output));
  }

  /**
   * Transcribes audio into the input language.
   *
   * @throws OpenAIException in case of API errors
   */
  public String createTranscript(TranscriptionRequest request) {
    long boundary = System.currentTimeMillis();
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder(boundary)
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

    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + boundary)
            .uri(baseUrl.resolve(Endpoint.TRANSCRIPTIONS.getPath()))
            .POST(multipartBodyPublisherBuilder.build())
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponseAsTree(httpResponse.body()).get("text").asText();
  }

  /**
   * Translates audio into English.
   *
   * @throws OpenAIException in case of API errors
   */
  public String createTranslation(TranslationRequest request) {
    long boundary = System.currentTimeMillis();
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder(boundary)
            .filePart("file", request.file())
            .textPart("model", request.model());
    request.prompt().ifPresent(prompt -> multipartBodyPublisherBuilder.textPart("prompt", prompt));
    request
        .temperature()
        .ifPresent(
            temperature -> multipartBodyPublisherBuilder.textPart("temperature", temperature));

    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + boundary)
            .uri(baseUrl.resolve(Endpoint.TRANSLATIONS.getPath()))
            .POST(multipartBodyPublisherBuilder.build())
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponseAsTree(httpResponse.body()).get("text").asText();
  }
}
