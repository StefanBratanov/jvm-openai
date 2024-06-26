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
 * Create messages within threads
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/messages">Messages</a>
 */
public final class MessagesClient extends OpenAIAssistantsClient {

  private static final String MESSAGES_SEGMENT = "/messages";

  private final URI baseUrl;

  MessagesClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a message.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadMessage createMessage(String threadId, CreateMessageRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId + MESSAGES_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadMessage.class);
  }

  /**
   * Returns a list of messages for a given thread.
   *
   * @param runId Filter messages by the run ID that generated them.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadMessages listMessages(
      String threadId,
      PaginationQueryParameters paginationQueryParameters,
      Optional<String> runId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + createQueryParameters(
                            paginationQueryParameters, Map.of("run_id", runId))))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadMessages.class);
  }

  public record PaginatedThreadMessages(
      List<ThreadMessage> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieve a message.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadMessage retrieveMessage(String threadId, String messageId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + "/"
                        + messageId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadMessage.class);
  }

  /**
   * Modifies a message.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadMessage modifyMessage(
      String threadId, String messageId, ModifyMessageRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + "/"
                        + messageId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadMessage.class);
  }

  /**
   * Deletes a message.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteMessage(String threadId, String messageId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + "/"
                        + messageId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
