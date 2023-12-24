package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/** Based on <a href="https://platform.openai.com/docs/api-reference/chat">Chat</a> */
public final class ChatClient extends OpenAIClient {

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

  public ChatResponse sendRequest(ChatRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ChatResponse.class);
  }

  public CompletableFuture<ChatResponse> sendRequestAsync(ChatRequest request) {
    HttpRequest httpRequest = createPostRequest(request);
    return sendHttpRequestAsync(httpRequest)
        .thenApply(httpResponse -> deserializeResponse(httpResponse.body(), ChatResponse.class));
  }

  private HttpRequest createPostRequest(ChatRequest request) {
    return newHttpRequestBuilder(
            "Content-Type", Constants.JSON_MEDIA_TYPE, "Accept", Constants.JSON_MEDIA_TYPE)
        .uri(endpoint)
        .POST(createBodyPublisher(request))
        .build();
  }
}
