package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Given a prompt and/or an input image, the model will generate a new image.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/images">Images</a>
 */
public final class ImagesClient extends OpenAIClient {

  private final URI baseUrl;

  ImagesClient(URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    super(apiKey, organization, httpClient);
    this.baseUrl = baseUrl;
  }

  /**
   * Creates an image given a prompt.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images createImage(CreateImageRequest request) {
    HttpRequest httpRequest = createImagePostRequest(request);

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Images.class);
  }

  /**
   * Same as {@link #createImage(CreateImageRequest)} but returns a response in a {@link
   * CompletableFuture}
   */
  public CompletableFuture<Images> createImageAsync(CreateImageRequest request) {
    HttpRequest httpRequest = createImagePostRequest(request);

    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), Images.class));
  }

  /**
   * Creates an edited or extended image given an original image and a prompt.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images editImage(EditImageRequest request) {
    HttpRequest httpRequest = editImagePostRequest(request);

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Images.class);
  }

  /**
   * Same as {@link #editImage(EditImageRequest)} but returns a response in a {@link
   * CompletableFuture}
   */
  public CompletableFuture<Images> editImageAsync(EditImageRequest request) {
    HttpRequest httpRequest = editImagePostRequest(request);

    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), Images.class));
  }

  /**
   * Creates a variation of a given image.
   *
   * @throws OpenAIException in case of API errors
   */
  public Images createImageVariation(CreateImageVariationRequest request) {
    HttpRequest httpRequest = createImageVariationPostRequest(request);

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Images.class);
  }

  /**
   * Same as {@link #createImageVariation(CreateImageVariationRequest)} but returns a response in a
   * {@link CompletableFuture}
   */
  public CompletableFuture<Images> createImageVariationAsync(CreateImageVariationRequest request) {
    HttpRequest httpRequest = createImageVariationPostRequest(request);

    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), Images.class));
  }

  private HttpRequest createImagePostRequest(CreateImageRequest request) {
    return newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
        .uri(baseUrl.resolve(Endpoint.IMAGE_GENERATION.getPath()))
        .POST(createBodyPublisher(request))
        .build();
  }

  private HttpRequest editImagePostRequest(EditImageRequest request) {
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder()
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

    MultipartBodyPublisher multipartBodyPublisher = multipartBodyPublisherBuilder.build();

    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
        .uri(baseUrl.resolve(Endpoint.IMAGE_EDIT.getPath()))
        .POST(multipartBodyPublisher)
        .build();
  }

  private HttpRequest createImageVariationPostRequest(CreateImageVariationRequest request) {
    MultipartBodyPublisher.Builder multipartBodyPublisherBuilder =
        MultipartBodyPublisher.newBuilder().filePart("image", request.image());
    request.model().ifPresent(model -> multipartBodyPublisherBuilder.textPart("model", model));
    request
        .responseFormat()
        .ifPresent(
            responseFormat ->
                multipartBodyPublisherBuilder.textPart("response_format", responseFormat));
    request.n().ifPresent(n -> multipartBodyPublisherBuilder.textPart("n", n));
    request.size().ifPresent(size -> multipartBodyPublisherBuilder.textPart("size", size));
    request.user().ifPresent(user -> multipartBodyPublisherBuilder.textPart("user", user));

    MultipartBodyPublisher multipartBodyPublisher = multipartBodyPublisherBuilder.build();

    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
        .uri(baseUrl.resolve(Endpoint.IMAGE_VARIATION.getPath()))
        .POST(multipartBodyPublisher)
        .build();
  }
}
