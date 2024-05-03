package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Vector stores are used to store files for use by the file_search tool.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/vector-stores">Vector
 * Stores</a>
 */
public final class VectorStoresClient extends OpenAIAssistantsClient {

  private final URI baseUrl;

  VectorStoresClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a vector store.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStore createVectorStore(CreateVectorStoreRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.VECTOR_STORES.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStore.class);
  }

  /**
   * Returns a list of vector stores.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedVectorStores listVectorStores(
      PaginationQueryParameters paginationQueryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + createQueryParameters(paginationQueryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedVectorStores.class);
  }

  public record PaginatedVectorStores(
      List<VectorStore> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves a vector store.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStore retrieveVectorStore(String vectorStoreId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.VECTOR_STORES.getPath() + "/" + vectorStoreId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStore.class);
  }

  /**
   * Create a vector store.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStore modifyVectorStore(String vectorStoreId, ModifyVectorStoreRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.VECTOR_STORES.getPath() + "/" + vectorStoreId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStore.class);
  }

  /**
   * Delete a vector store.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteVectorStore(String vectorStoreId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.VECTOR_STORES.getPath() + "/" + vectorStoreId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
