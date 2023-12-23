package com.stefanbratanov.chatjpt;

import static com.stefanbratanov.chatjpt.Utils.getAuthorizationHeader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Subclasses should be based on one of the endpoints defined at <a
 * href="https://platform.openai.com/docs/api-reference">API Reference</a>
 */
abstract class OpenAIClient<Req, Res> {

  private final URI endpoint;
  private final String apiKey;
  private final HttpClient httpClient;

  OpenAIClient(URI endpoint, String apiKey, HttpClient httpClient) {
    this.endpoint = endpoint;
    this.apiKey = apiKey;
    this.httpClient = httpClient;
  }

  public Res sendRequest(Req request) {
    HttpRequest httpRequest = createHttpRequest(request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    validateResponse(httpResponse);
    return deserializeResponse(httpResponse);
  }

  public CompletableFuture<Res> sendRequestAsync(Req request) {
    HttpRequest httpRequest = createHttpRequest(request);
    return httpClient
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
        .thenApply(
            httpResponse -> {
              validateResponse(httpResponse);
              return deserializeResponse(httpResponse);
            });
  }

  abstract String[] getHeaders();

  abstract HttpRequest.BodyPublisher createBodyPublisher(Req request);

  abstract Res deserializeResponse(HttpResponse<byte[]> httpResponse);

  private HttpRequest createHttpRequest(Req request) {
    return HttpRequest.newBuilder()
        .headers(getAuthorizationHeader(apiKey))
        .headers(getHeaders())
        .uri(endpoint)
        .POST(createBodyPublisher(request))
        .build();
  }

  private HttpResponse<byte[]> sendHttpRequest(HttpRequest httpRequest) {
    try {
      return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  // TODO: implement
  private void validateResponse(HttpResponse<byte[]> httpResponse) {}
}
