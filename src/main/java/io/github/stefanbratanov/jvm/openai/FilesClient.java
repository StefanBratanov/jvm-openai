package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Files are used to upload documents that can be used with features like Assistants and
 * Fine-tuning.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/files">Files</a>
 */
public final class FilesClient extends OpenAIClient {

  private final URI baseUrl;

  FilesClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Upload a file that can be used across various endpoints. The size of all the files uploaded by
   * one organization can be up to 100 GB.
   *
   * @throws OpenAIException in case of API errors
   */
  public File uploadFile(UploadFileRequest request) {
    MultipartBodyPublisher multipartBodyPublisher =
        MultipartBodyPublisher.newBuilder()
            .filePart("file", request.file())
            .textPart("purpose", request.purpose())
            .build();

    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
            .uri(baseUrl.resolve(Endpoint.FILES.getPath()))
            .POST(multipartBodyPublisher)
            .build();

    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);

    return deserializeResponse(httpResponse.body(), File.class);
  }

  /**
   * Returns a list of files that belong to the user's organization
   *
   * @throws OpenAIException in case of API errors
   */
  public List<File> listFiles() {
    HttpRequest httpRequest =
        newHttpRequestBuilder().uri(baseUrl.resolve(Endpoint.FILES.getPath())).GET().build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeDataInResponseAsList(httpResponse.body(), File.class);
  }

  /**
   * Returns information about a specific file
   *
   * @throws OpenAIException in case of API errors
   */
  public File retrieveFile(String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.FILES.getPath() + "/" + fileId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), File.class);
  }

  /**
   * Delete a file
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteFile(String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.FILES.getPath() + "/" + fileId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }

  /**
   * Returns the contents of the specified file
   *
   * @throws OpenAIException in case of API errors
   */
  public byte[] retrieveFileContent(String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.FILES.getPath() + "/" + fileId + "/content"))
            .GET()
            .build();
    return sendHttpRequest(httpRequest).body();
  }
}
