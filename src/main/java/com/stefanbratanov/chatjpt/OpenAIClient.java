package com.stefanbratanov.chatjpt;

import static com.stefanbratanov.chatjpt.Utils.getAuthorizationHeader;
import static com.stefanbratanov.chatjpt.Utils.validateHttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Subclasses should be based on one of the endpoints defined at <a
 * href="https://platform.openai.com/docs/api-reference">API Reference</a>
 */
abstract class OpenAIClient {

  private final URI endpoint;
  private final String apiKey;
  private final Optional<String> organization;

  protected final HttpClient httpClient;
  protected final ObjectMapper objectMapper;

  OpenAIClient(
      URI endpoint,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    this.endpoint = endpoint;
    this.apiKey = apiKey;
    this.organization = organization;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  abstract String[] getHeaders();

  protected HttpRequest.Builder newHttpRequestBuilder() {
    HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(endpoint);
    // set headers
    httpRequestBuilder.headers(getAuthorizationHeader(apiKey));
    organization.ifPresent(org -> httpRequestBuilder.header("OpenAI-Organization", org));
    httpRequestBuilder.headers(getHeaders());

    return httpRequestBuilder;
  }

  protected <T> HttpRequest.BodyPublisher createBodyPublisher(T body) {
    try {
      return HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body));
    } catch (JsonProcessingException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  protected HttpResponse<byte[]> sendHttpRequest(HttpRequest httpRequest) {
    try {
      HttpResponse<byte[]> httpResponse =
          httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
      validateHttpResponse(httpResponse, objectMapper);
      return httpResponse;
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected CompletableFuture<HttpResponse<byte[]>> sendHttpRequestAsync(HttpRequest httpRequest) {
    return httpClient
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
        .thenApply(
            httpResponse -> {
              validateHttpResponse(httpResponse, objectMapper);
              return httpResponse;
            });
  }

  protected <T> T deserializeResponse(HttpResponse<byte[]> httpResponse, Class<T> responseClass) {
    try {
      return objectMapper.readValue(httpResponse.body(), responseClass);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
