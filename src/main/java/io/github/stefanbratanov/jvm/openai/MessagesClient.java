package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/**
 * Create messages within threads
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/messages">Messages</a>
 */
public final class MessagesClient extends OpenAIAssistantsClient {

  private static final String MESSAGES_SEGMENT = "/messages";
  private static final String FILES_SEGMENT = "/files";

  private final URI baseUrl;

  MessagesClient(URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    super(apiKey, organization, httpClient);
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
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadMessages listMessages(
      String threadId, PaginationQueryParameters queryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + createQueryParameters(queryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadMessages.class);
  }

  public record PaginatedThreadMessages(
      List<ThreadMessage> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Returns a list of message files.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadMessageFiles listMessageFiles(
      String threadId, String messageId, PaginationQueryParameters queryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + "/"
                        + messageId
                        + FILES_SEGMENT
                        + createQueryParameters(queryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadMessageFiles.class);
  }

  public record PaginatedThreadMessageFiles(
      List<ThreadMessageFile> data, String firstId, String lastId, boolean hasMore) {}

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
   * Retrieves a message file.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadMessageFile retrieveMessageFile(String threadId, String messageId, String fileId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + MESSAGES_SEGMENT
                        + "/"
                        + messageId
                        + FILES_SEGMENT
                        + "/"
                        + fileId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadMessageFile.class);
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
}
