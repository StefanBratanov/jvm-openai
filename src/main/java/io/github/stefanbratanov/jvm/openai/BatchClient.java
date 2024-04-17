package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Create large batches of API requests to run asynchronously.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/batch">Batch</a>
 */
public final class BatchClient extends OpenAIClient {

  private final URI baseUrl;

  BatchClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Creates and executes a batch from an uploaded file of requests
   *
   * @throws OpenAIException in case of API errors
   */
  public Batch createBatch(CreateBatchRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.BATCHES.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Batch.class);
  }

  /**
   * Retrieves a batch
   *
   * @throws OpenAIException in case of API errors
   */
  public Batch retrieveBatch(String batchId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.BATCHES.getPath() + "/" + batchId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Batch.class);
  }

  /**
   * Cancels an in-progress batch
   *
   * @return The Batch object matching the specified ID.
   * @throws OpenAIException in case of API errors
   */
  public Batch cancelBatch(String batchId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.BATCHES.getPath() + "/" + batchId + "/cancel"))
            .POST(BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Batch.class);
  }
}
