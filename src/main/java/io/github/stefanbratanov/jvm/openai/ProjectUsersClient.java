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
 * Manage users within a project, including adding, updating roles, and removing users. Users cannot
 * be removed from the Default project, unless they are being removed from the organization.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/project-users">Project
 * Users</a>
 */
public final class ProjectUsersClient extends OpenAIClient {

  private static final String USERS_SEGMENT = "/users";

  private final URI baseUrl;

  ProjectUsersClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of users in the project.
   *
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedProjectUsers listProjectUsers(
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
                        + USERS_SEGMENT
                        + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedProjectUsers.class);
  }

  public record PaginatedProjectUsers(
      List<ProjectUser> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Adds a user to the project. Users must already be members of the organization to be added to a
   * project.
   *
   * @param projectId The ID of the project.
   * @throws OpenAIException in case of API errors
   */
  public ProjectUser createProjectUser(String projectId, CreateProjectUserRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath() + "/" + projectId + USERS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectUser.class);
  }

  /**
   * Retrieves a user in the project.
   *
   * @param projectId The ID of the project.
   * @param userId The ID of the user.
   * @throws OpenAIException in case of API errors
   */
  public ProjectUser retrieveProjectUser(String projectId, String userId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + USERS_SEGMENT + "/" + userId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectUser.class);
  }

  /**
   * Modifies a user's role in the project.
   *
   * @param projectId The ID of the project.
   * @param userId The ID of the user.
   * @throws OpenAIException in case of API errors
   */
  public ProjectUser modifyProjectUser(
      String projectId, String userId, ModifyProjectUserRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + USERS_SEGMENT + "/" + userId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ProjectUser.class);
  }

  /**
   * Deletes a user from the project.
   *
   * @param projectId The ID of the project.
   * @param userId The ID of the user.
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteProjectUser(String projectId, String userId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.PROJECTS.getPath() + "/" + projectId + USERS_SEGMENT + "/" + userId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
