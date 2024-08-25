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
 * Invite and manage invitations for an organization. Invited users are automatically added to the
 * Default project.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/invite">Invites</a>
 */
public class InvitesClient extends OpenAIClient {

  private final URI baseUrl;

  InvitesClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of invites in the organization.
   *
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedInvites listInvites(Optional<String> after, Optional<Integer> limit) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.INVITES.getPath() + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedInvites.class);
  }

  public record PaginatedInvites(
      List<Invite> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Create an invite for a user to the organization. The invite must be accepted by the user before
   * they have access to the organization.
   *
   * @throws OpenAIException in case of API errors
   */
  public Invite createInvite(InviteRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.INVITES.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Invite.class);
  }

  /**
   * Retrieves an invite.
   *
   * @param inviteId The ID of the invite to retrieve.
   * @throws OpenAIException in case of API errors
   */
  public Invite retrieveInvite(String inviteId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.INVITES.getPath() + "/" + inviteId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Invite.class);
  }

  /**
   * Delete an invite. If the invite has already been accepted, it cannot be deleted.
   *
   * @param inviteId The ID of the invite to delete.
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteInvite(String inviteId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.INVITES.getPath() + "/" + inviteId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
