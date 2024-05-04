package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Vector store files represent files inside a vector store.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/vector-stores-files">Vector
 * Store Files</a>
 */
public final class VectorStoreFilesClient extends OpenAIAssistantsClient {

  private static final String FILES_SEGMENT = "/files";

  private final URI baseUrl;

  VectorStoreFilesClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a vector store file by attaching a File to a vector store.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStoreFile createVectorStoreFile(
      String vectorStoreId, CreateVectorStoreFileRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath() + "/" + vectorStoreId + FILES_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStoreFile.class);
  }

  /**
   * Returns a list of vector store files.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedVectorStoreFiles listVectorStoreFiles(
      String vectorStoreId, PaginationQueryParameters paginationQueryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILES_SEGMENT
                        + createQueryParameters(paginationQueryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedVectorStoreFiles.class);
  }

  public record PaginatedVectorStoreFiles(
      List<VectorStoreFile> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves a vector store file.
   *
   * @throws OpenAIException in case of API errors
   */
  public VectorStoreFile retrieveVectorStoreFile(String vectorStoreId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILES_SEGMENT
                        + "/"
                        + fileId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), VectorStoreFile.class);
  }

  /**
   * Delete a vector store file. This will remove the file from the vector store but the file itself
   * will not be deleted. To delete the file, use the delete file endpoint.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteVectorStoreFile(String vectorStoreId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.VECTOR_STORES.getPath()
                        + "/"
                        + vectorStoreId
                        + FILES_SEGMENT
                        + "/"
                        + fileId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
