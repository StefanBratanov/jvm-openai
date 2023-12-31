package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/** Based on <a href="https://platform.openai.com/docs/api-reference/images">Images</a> */
public final class ImagesClient extends OpenAIClient {

  private final URI baseUrl;

  ImagesClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    this.baseUrl = baseUrl;
  }

  /**
   * Creates an image given a prompt.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images createImage(CreateImageRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.IMAGE_GENERATION.getPath()))
            .POST(createBodyPublisher(request))
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponse(httpResponse.body(), Images.class);
  }

  /**
   * Creates an edited or extended image given an original image and a prompt.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images editImage(EditImageRequest request) {
    long boundary = System.currentTimeMillis();
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder(boundary)
            .filePart("image", request.image())
            .textPart("prompt", request.prompt());
    request.mask().ifPresent(mask -> multipartBodyPublisherBuilder.filePart("mask", mask));
    request.model().ifPresent(model -> multipartBodyPublisherBuilder.textPart("model", model));
    request.n().ifPresent(n -> multipartBodyPublisherBuilder.textPart("n", n));
    request.size().ifPresent(size -> multipartBodyPublisherBuilder.textPart("size", size));
    request
        .responseFormat()
        .ifPresent(
            responseFormat ->
                multipartBodyPublisherBuilder.textPart("response_format", responseFormat));
    request.user().ifPresent(user -> multipartBodyPublisherBuilder.textPart("user", user));

    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + boundary)
            .uri(baseUrl.resolve(Endpoint.IMAGE_EDIT.getPath()))
            .POST(multipartBodyPublisherBuilder.build())
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponse(httpResponse.body(), Images.class);
  }

  /**
   * Creates a variation of a given image.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images createImageVariation(CreateImageVariationRequest request) {
    long boundary = System.currentTimeMillis();
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder(boundary).filePart("image", request.image());
    request.model().ifPresent(model -> multipartBodyPublisherBuilder.textPart("model", model));
    request
        .responseFormat()
        .ifPresent(
            responseFormat ->
                multipartBodyPublisherBuilder.textPart("response_format", responseFormat));
    request.n().ifPresent(n -> multipartBodyPublisherBuilder.textPart("n", n));
    request.size().ifPresent(size -> multipartBodyPublisherBuilder.textPart("size", size));
    request.user().ifPresent(user -> multipartBodyPublisherBuilder.textPart("user", user));

    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + boundary)
            .uri(baseUrl.resolve(Endpoint.IMAGE_VARIATION.getPath()))
            .POST(multipartBodyPublisherBuilder.build())
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponse(httpResponse.body(), Images.class);
  }
}
