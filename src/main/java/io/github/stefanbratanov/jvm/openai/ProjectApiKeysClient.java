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
 * Manage API keys for a given project. Supports listing and deleting keys for users. This API does
 * not allow issuing keys for users, as users need to authorize themselves to generate keys.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/project-api-keys">Project API
 * Keys</a>
 */
public final class ProjectApiKeysClient extends OpenAIClient {

  private static final String API_KEYS_SEGMENT = "/api_keys";

  private final URI baseUrl;

  ProjectApiKeysClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of API keys in the project.
   *
   * @param projectId The ID of the project.
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedProjectApiKeys listProjectApiKeys(
      String projectId, Optional<String> after, Optional<Integer> limit) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath()
                        + "/"
                        + projectId
                        + API_KEYS_SEGMENT
                        + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedProjectApiKeys.class);
  }

  public record PaginatedProjectApiKeys(
      List<ProjectApiKey> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves an API key in the project.
   *
   * @param projectId The ID of the project.
   * @param keyId The ID of the API key.
   * @throws OpenAIException in case of API errors
   */
  public ProjectApiKey retrieveProjectApiKey(String projectId, String keyId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + API_KEYS_SEGMENT + "/" + keyId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectApiKey.class);
  }

  /**
   * Deletes an API key from the project.
   *
   * @param projectId The ID of the project.
   * @param keyId The ID of the API key.
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteProjectApiKey(String projectId, String keyId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + API_KEYS_SEGMENT + "/" + keyId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
