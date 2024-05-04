package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Vector store file batches represent operations to add multiple files to a vector store.
 *
 * <p>Based on <a
 * href="https://platform.openai.com/docs/api-reference/vector-stores-file-batches">Vector Store
 * File Batches</a>
 */
public final class VectorStoreFileBatchesClient extends OpenAIAssistantsClient {

  private static final String FILE_BATCHES_SEGMENT = "/file_batches";

  private final URI baseUrl;

  VectorStoreFileBatchesClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a vector store file batch.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStoreFileBatch createVectorStoreFileBatch(
      String vectorStoreId, CreateVectorStoreFileBatchRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath() + "/" + vectorStoreId + FILE_BATCHES_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStoreFileBatch.class);
  }

  /**
   * Retrieves a vector store file batch.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStoreFileBatch retrieveVectorStoreFileBatch(String vectorStoreId, String batchId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILE_BATCHES_SEGMENT
                        + "/"
                        + batchId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStoreFileBatch.class);
  }

  /**
   * Cancel a vector store file batch. This attempts to cancel the processing of files in this batch
   * as soon as possible.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStoreFileBatch cancelVectorStoreFileBatch(String vectorStoreId, String batchId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILE_BATCHES_SEGMENT
                        + "/"
                        + batchId
                        + "/cancel"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStoreFileBatch.class);
  }

  /**
   * Returns a list of vector store files in a batch.
   *
   * @param filter Filter by file status
   * @throws OpenAIException in case of API errors
   */
  public PaginatedVectorStoreFiles listVectorStoreFilesInBatch(
      String vectorStoreId,
      String batchId,
      PaginationQueryParameters paginationQueryParameters,
      Optional<String> filter) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILE_BATCHES_SEGMENT
                        + "/"
                        + batchId
                        + "/files"
                        + createQueryParameters(
                            paginationQueryParameters, Map.of("filter", filter))))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedVectorStoreFiles.class);
  }

  public record PaginatedVectorStoreFiles(
      List<VectorStoreFile> data, String firstId, String lastId, boolean hasMore) {}
}
