package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/** Based on <a href="https://platform.openai.com/docs/api-reference/threads">Threads</a> */
public final class ThreadsClient extends OpenAIAssistantsClient {

  private final URI baseUrl;

  ThreadsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public Thread createThread(CreateThreadRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Thread.class);
  }

  /**
   * Retrieves a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public Thread retrieveThread(String threadId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Thread.class);
  }

  /**
   * Modifies a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public Thread modifyThread(String threadId, ModifyThreadRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Thread.class);
  }

  /**
   * Delete a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteThread(String threadId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
