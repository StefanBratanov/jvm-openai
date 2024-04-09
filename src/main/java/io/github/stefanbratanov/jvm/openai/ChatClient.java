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

  private final URI endpoint;

  ChatClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
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
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createPostRequest(request);
    return getStreamedChatCompletionChunks(httpRequest);
  }

  /**
   * Same as {@link #streamChatCompletion(CreateChatCompletionRequest)} but can pass a {@link
   * ChatCompletionStreamSubscriber} implementation instead of using a {@link
   * Stream<ChatCompletionChunk>}
   */
  public void streamChatCompletion(
      CreateChatCompletionRequest request, ChatCompletionStreamSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createPostRequest(request);
    CompletableFuture.supplyAsync(() -> getStreamedChatCompletionChunks(httpRequest))
        .thenAccept(chatCompletionChunks -> chatCompletionChunks.forEach(subscriber::onChunk))
        .whenComplete(
            (result, ex) -> {
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

  private Stream<ChatCompletionChunk> getStreamedChatCompletionChunks(HttpRequest httpRequest) {
    return streamServerSentEvents(httpRequest)
        .map(
            sseEvent -> {
              String data = sseEvent.substring(sseEvent.indexOf("{"));
              return deserializeData(data, ChatCompletionChunk.class);
            });
  }
}
