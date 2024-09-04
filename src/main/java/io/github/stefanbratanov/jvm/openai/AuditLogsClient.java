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
 * Logs of user actions and configuration changes within this organization.
 *
 * <p>To log events, you must activate logging in the Organization Settings. Once activated, for
 * security reasons, logging cannot be deactivated.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/audit-logs">Audit Logs</a>
 */
public final class AuditLogsClient extends OpenAIClient {

  private final URI baseUrl;

  AuditLogsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * List user actions and configuration changes within this organization.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedAuditLogs listAuditLogs(ListAuditLogsQueryParameters queryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.AUDIT_LOGS.getPath() + buildQueryParameters(queryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedAuditLogs.class);
  }

  public record PaginatedAuditLogs(
      List<AuditLog> data, String firstId, String lastId, boolean hasMore) {}

  private String buildQueryParameters(ListAuditLogsQueryParameters queryParameters) {
    return createQueryParameters(
        Map.of(
            "effective_at",
            queryParameters.effectiveAt(),
            "project_ids[]",
            queryParameters.projectIds(),
            "event_types[]",
            queryParameters.eventTypes(),
            "actor_ids[]",
            queryParameters.actorIds(),
            "actor_emails[]",
            queryParameters.actorEmails(),
            "resource_ids[]",
            queryParameters.actorIds(),
            Constants.LIMIT_QUERY_PARAMETER,
            queryParameters.limit(),
            Constants.AFTER_QUERY_PARAMETER,
            queryParameters.after(),
            Constants.BEFORE_QUERY_PARAMETER,
            queryParameters.before()));
  }
}
