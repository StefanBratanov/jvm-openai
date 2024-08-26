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
 * Manage users and their role in an organization. Users will be automatically added to the Default
 * project.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/users">Users</a>
 */
public final class UsersClient extends OpenAIClient {

  private final URI baseUrl;

  UsersClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Lists all of the users in the organization.
   *
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedUsers listUsers(Optional<String> after, Optional<Integer> limit) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.USERS.getPath() + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedUsers.class);
  }

  public record PaginatedUsers(List<User> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Modifies a user's role in the organization.
   *
   * @throws OpenAIException in case of API errors
   */
  public User modifyUser(ModifyUserRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.USERS.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), User.class);
  }

  /**
   * Retrieves a user by their identifier.
   *
   * @param userId The ID of the user.
   * @throws OpenAIException in case of API errors
   */
  public User retrieveUser(String userId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.USERS.getPath() + "/" + userId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), User.class);
  }

  /**
   * Deletes a user from the organization.
   *
   * @param userId The ID of the user.
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteUser(String userId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.USERS.getPath() + "/" + userId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
