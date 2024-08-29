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
 * Manage service accounts within a project. A service account is a bot user that is not associated
 * with a user. If a user leaves an organization, their keys and membership in projects will no
 * longer work. Service accounts do not have this limitation. However, service accounts can also be
 * deleted from a project.
 *
 * <p>Based on <a
 * href="https://platform.openai.com/docs/api-reference/project-service-accounts">Project Service
 * Accounts</a>
 */
public final class ProjectServiceAccountsClient extends OpenAIClient {

  private static final String SERVICE_ACCOUNTS_SEGMENT = "/service_accounts";

  private final URI baseUrl;

  ProjectServiceAccountsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of service accounts in the project.
   *
   * @param projectId The ID of the project.
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedProjectServiceAccounts listProjectServiceAccounts(
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
                        + SERVICE_ACCOUNTS_SEGMENT
                        + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedProjectServiceAccounts.class);
  }

  public record PaginatedProjectServiceAccounts(
      List<ProjectServiceAccount> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Creates a new service account in the project. This also returns an unredacted API key for the
   * service account.
   *
   * @param projectId The ID of the project.
   * @throws OpenAIException in case of API errors
   */
  public ProjectServiceAccountCreateResponse createProjectServiceAccount(
      String projectId, CreateProjectServiceAccountRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + SERVICE_ACCOUNTS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectServiceAccountCreateResponse.class);
  }

  public record ProjectServiceAccountCreateResponse(
      String id, String name, String role, long createdAt, ApiKey apiKey) {}

  public record ApiKey(String id, String name, String value, long createdAt) {}

  /**
   * Retrieves a service account in the project.
   *
   * @param projectId The ID of the project.
   * @param serviceAccountId The ID of the service account.
   * @throws OpenAIException in case of API errors
   */
  public ProjectServiceAccount retrieveProjectServiceAccount(
      String projectId, String serviceAccountId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath()
                        + "/"
                        + projectId
                        + SERVICE_ACCOUNTS_SEGMENT
                        + "/"
                        + serviceAccountId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectServiceAccount.class);
  }

  /**
   * Deletes a service account from the project.
   *
   * @param projectId The ID of the project.
   * @param serviceAccountId The ID of the service account.
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteProjectServiceAccount(String projectId, String serviceAccountId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath()
                        + "/"
                        + projectId
                        + SERVICE_ACCOUNTS_SEGMENT
                        + "/"
                        + serviceAccountId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
