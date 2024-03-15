package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
      Optional<Duration> requestTimeout) {
    super(apiKey, organization, httpClient, requestTimeout);
    endpoint = baseUrl.resolve(Endpoint.CHAT.getPath());
  }

  /**
   * Creates a model response for the given chat conversation.
   *
   * @throws OpenAIException in case of API errors
   */
  public ChatCompletion createChatCompletion(CreateChatCompletionRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ChatCompletion.class);
  }

  /**
   * Same as {@link #createChatCompletion(CreateChatCompletionRequest)} but returns a response in a
   * {@link CompletableFuture}
   */
  public CompletableFuture<ChatCompletion> createChatCompletionAsync(
      CreateChatCompletionRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), ChatCompletion.class));
  }

  /**
   * Stream model responses back in order to allow partial results for the given request.
   *
   * @param request the request should be configured with {@link
   *     CreateChatCompletionRequest.Builder#stream(boolean)} set to true
   * @throws OpenAIException in case of API errors
   */
  public Stream<ChatCompletionChunk> streamChatCompletion(CreateChatCompletionRequest request) {
    validateStreamRequest(request);
    HttpRequest httpRequest = createPostRequest(request);
    return getStreamedResponses(httpRequest);
  }

  /**
   * Same as {@link #streamChatCompletion(CreateChatCompletionRequest)} but can pass a {@link
   * StreamChatCompletionSubscriber} implementation instead of using a {@link
   * Stream<ChatCompletionChunk>}
   */
  public void streamChatCompletion(
      CreateChatCompletionRequest request, StreamChatCompletionSubscriber subscriber) {
    validateStreamRequest(request);
    HttpRequest httpRequest = createPostRequest(request);
    CompletableFuture.supplyAsync(() -> getStreamedResponses(httpRequest))
        .thenAccept(streamedResponses -> streamedResponses.forEach(subscriber::onChunk))
        .whenComplete(
            (__, ex) -> {
              if (ex != null) {
                subscriber.onException(ex);
              }
              subscriber.onComplete();
            });
  }

  private HttpRequest createPostRequest(CreateChatCompletionRequest request) {
    return newHttpRequestBuilder(
            Constants.CONTENT_TYPE_HEADER,
            Constants.JSON_MEDIA_TYPE,
            Constants.ACCEPT_HEADER,
            Constants.JSON_MEDIA_TYPE)
        .uri(endpoint)
        .POST(createBodyPublisher(request))
        .build();
  }

  private void validateStreamRequest(CreateChatCompletionRequest request) {
    if (!request.stream().orElse(false)) {
      throw new IllegalArgumentException("stream must be set to true when requesting a stream");
    }
  }

  private Stream<ChatCompletionChunk> getStreamedResponses(HttpRequest httpRequest) {
    return sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofLines())
        .body()
        .filter(sseEvent -> !sseEvent.isBlank())
        .takeWhile(sseEvent -> !sseEvent.equals(STREAM_TERMINATION))
        .map(
            sseEvent -> {
              String chatChunkResponse = sseEvent.substring(sseEvent.indexOf("{"));
              return deserializeResponse(chatChunkResponse.getBytes(), ChatCompletionChunk.class);
            });
  }
}
