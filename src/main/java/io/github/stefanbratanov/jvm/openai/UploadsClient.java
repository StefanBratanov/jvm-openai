package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

/**
 * Allows you to upload large files in multiple parts.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/uploads">Uploads</a>
 */
public final class UploadsClient extends OpenAIClient {

  private static final String PARTS_SEGMENT = "/parts";
  private static final String COMPLETE_SEGMENT = "/complete";
  private static final String CANCEL_SEGMENT = "/cancel";

  private final URI baseUrl;

  UploadsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Creates an intermediate Upload object that you can add Parts to. Currently, an Upload can
   * accept at most 8 GB in total and expires after an hour after you create it.
   *
   * <p>Once you complete the Upload, we will create a File object that contains all the parts you
   * uploaded. This File is usable in the rest of our platform as a regular File object.
   *
   * @throws OpenAIException in case of API errors
   */
  public Upload createUpload(CreateUploadRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.UPLOADS.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Upload.class);
  }

  /**
   * Adds a Part to an Upload object. A Part represents a chunk of bytes from the file you are
   * trying to upload.
   *
   * <p>Each Part can be at most 64 MB, and you can add Parts until you hit the Upload maximum of 8
   * GB.
   *
   * <p>It is possible to add multiple Parts in parallel. You can decide the intended order of the
   * Parts when you complete the Upload.
   *
   * @param uploadId The ID of the Upload.
   * @param data The chunk of bytes for this Part.
   * @throws OpenAIException in case of API errors
   */
  public UploadPart addUploadPart(String uploadId, Path data) {
    MultipartBodyPublisher multipartBodyPublisher =
        MultipartBodyPublisher.newBuilder().filePart("data", data).build();
    HttpRequest httpRequest =
        newHttpRequestBuilder(
                Constants.CONTENT_TYPE_HEADER, multipartBodyPublisher.getContentTypeHeader())
            .uri(baseUrl.resolve(Endpoint.UPLOADS.getPath() + "/" + uploadId + PARTS_SEGMENT))
            .POST(multipartBodyPublisher)
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), UploadPart.class);
  }

  /**
   * Completes the Upload.
   *
   * <p>Within the returned Upload object, there is a nested File object that is ready to use in the
   * rest of the platform.
   *
   * <p>You can specify the order of the Parts by passing in an ordered list of the Part IDs.
   *
   * <p>The number of bytes uploaded upon completion must match the number of bytes initially
   * specified when creating the Upload object. No Parts may be added after an Upload is completed.
   *
   * @param uploadId The ID of the Upload.
   * @throws OpenAIException in case of API errors
   */
  public Upload completeUpload(String uploadId, CompleteUploadRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.UPLOADS.getPath() + "/" + uploadId + COMPLETE_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Upload.class);
  }

  /**
   * Cancels the Upload. No Parts may be added after an Upload is cancelled.
   *
   * @param uploadId The ID of the Upload.
   * @throws OpenAIException in case of API errors
   */
  public Upload cancelUpload(String uploadId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.UPLOADS.getPath() + "/" + uploadId + CANCEL_SEGMENT))
            .POST(BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Upload.class);
  }
}
