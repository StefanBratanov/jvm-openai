package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Build assistants that can call models and use tools to perform tasks.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/assistants">Assistants</a>
 */
public final class AssistantsClient extends OpenAIAssistantsClient {

  private static final String FILES_SEGMENT = "/files";

  private final URI baseUrl;

  AssistantsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create an assistant with a model and instructions.
   *
   * @throws OpenAIException in case of API errors
   */
  public Assistant createAssistant(CreateAssistantRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.ASSISTANTS.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Assistant.class);
  }

  /**
   * Create an assistant file by attaching a File to an assistant.
   *
   * @throws OpenAIException in case of API errors
   */
  public AssistantFile createAssistantFile(String assistantId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.ASSISTANTS.getPath() + "/" + assistantId + FILES_SEGMENT))
            .POST(HttpRequest.BodyPublishers.ofString("{\"file_id\":\"" + fileId + "\"}"))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), AssistantFile.class);
  }

  /**
   * Returns a list of assistants.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedAssistants listAssistants(PaginationQueryParameters paginationQueryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.ASSISTANTS.getPath()
                        + createQueryParameters(paginationQueryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedAssistants.class);
  }

  public record PaginatedAssistants(
      List<Assistant> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Returns a list of assistant files.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedAssistantFiles listAssistantFiles(
      String assistantId, PaginationQueryParameters paginationQueryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.ASSISTANTS.getPath()
                        + "/"
                        + assistantId
                        + FILES_SEGMENT
                        + createQueryParameters(paginationQueryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedAssistantFiles.class);
  }

  public record PaginatedAssistantFiles(
      List<AssistantFile> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves an assistant.
   *
   * @throws OpenAIException in case of API errors
   */
  public Assistant retrieveAssistant(String assistantId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.ASSISTANTS.getPath() + "/" + assistantId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Assistant.class);
  }

  /**
   * Retrieves an AssistantFile.
   *
   * @throws OpenAIException in case of API errors
   */
  public AssistantFile retrieveAssistantFile(String assistantId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.ASSISTANTS.getPath()
                        + "/"
                        + assistantId
                        + FILES_SEGMENT
                        + "/"
                        + fileId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), AssistantFile.class);
  }

  /**
   * Modifies an assistant.
   *
   * @throws OpenAIException in case of API errors
   */
  public Assistant modifyAssistant(String assistantId, ModifyAssistantRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.ASSISTANTS.getPath() + "/" + assistantId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Assistant.class);
  }

  /**
   * Delete an assistant.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteAssistant(String assistantId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.ASSISTANTS.getPath() + "/" + assistantId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }

  /**
   * Delete an assistant file.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteAssistantFile(String assistantId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.ASSISTANTS.getPath()
                        + "/"
                        + assistantId
                        + FILES_SEGMENT
                        + "/"
                        + fileId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
