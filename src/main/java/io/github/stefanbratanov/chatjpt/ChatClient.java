package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Given a list of messages comprising a conversation, the model will return a response.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/chat">Chat</a>
 */
public final class ChatClient extends OpenAIClient {

  private static final String STREAM_TERMINATION = "data: [DONE]";

  private final URI endpoint;

  ChatClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    endpoint = baseUrl.resolve(Endpoint.CHAT.getPath());
  }

  /**
   * Creates a model response for the given chat conversation.
   *
   * @throws OpenAIException in case of API errors
   */
  public ChatResponse sendRequest(ChatRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ChatResponse.class);
  }

  /**
   * Same as {@link #sendRequest(ChatRequest)} but returns a response in a {@link CompletableFuture}
   */
  public CompletableFuture<ChatResponse> sendRequestAsync(ChatRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), ChatResponse.class));
  }

  /**
   * Stream model responses back in order to allow partial results for the given request.
   *
   * @param request the request should be configured with {@link
   *     ChatRequest.Builder#stream(boolean)} set to true
   * @throws OpenAIException in case of API errors
   */
  public Stream<ChatChunkResponse> sendStreamRequest(ChatRequest request) {
    if (!request.stream().orElse(false)) {
      throw new IllegalArgumentException("stream must be set to true when requesting a stream");
    }
    HttpRequest httpRequest = createPostRequest(request);
    return sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofLines())
        .body()
        .filter(sseEvent -> !sseEvent.isBlank())
        .takeWhile(sseEvent -> !sseEvent.equals(STREAM_TERMINATION))
        .map(
            sseEvent -> {
              String chatChunkResponse = sseEvent.substring(sseEvent.indexOf("{"));
              return deserializeResponse(chatChunkResponse.getBytes(), ChatChunkResponse.class);
            });
  }

  private HttpRequest createPostRequest(ChatRequest request) {
    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER,
            Constants.JSON_MEDIA_TYPE,
            Constants.ACCEPT_HEADER,
            Constants.JSON_MEDIA_TYPE)
        .uri(endpoint)
        .POST(createBodyPublisher(request))
        .build();
  }
}
