package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Subclasses should be based on one of the endpoints defined at <a
 * href="https://platform.openai.com/docs/api-reference">API Reference - OpenAI API</a>
 */
abstract class OpenAIClient {

  private final String[] authenticationHeaders;

  protected final HttpClient httpClient;
  protected final ObjectMapper objectMapper;

  OpenAIClient(
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    this.authenticationHeaders = getAuthenticationHeaders(apiKey, organization);
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  HttpRequest.Builder newHttpRequestBuilder(String... headers) {
    HttpRequest.Builder httpRequestBuilder =
        HttpRequest.newBuilder().headers(authenticationHeaders);
    if (headers.length > 0) {
      httpRequestBuilder.headers(headers);
    }
    return httpRequestBuilder;
  }

  <T> HttpRequest.BodyPublisher createBodyPublisher(T body) {
    try {
      return HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body));
    } catch (JsonProcessingException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  HttpResponse<byte[]> sendHttpRequest(HttpRequest httpRequest) {
    return sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
  }

  <T> HttpResponse<T> sendHttpRequest(
      HttpRequest httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) {
    try {
      HttpResponse<T> httpResponse = httpClient.send(httpRequest, responseBodyHandler);
      validateHttpResponse(httpResponse);
      return httpResponse;
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  CompletableFuture<HttpResponse<byte[]>> sendHttpRequestAsync(HttpRequest httpRequest) {
    return sendHttpRequestAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
  }

  <T> CompletableFuture<HttpResponse<T>> sendHttpRequestAsync(
      HttpRequest httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) {
    return httpClient
        .sendAsync(httpRequest, responseBodyHandler)
        .thenApply(
            httpResponse -> {
              validateHttpResponse(httpResponse);
              return httpResponse;
            });
  }

  void validateHttpResponse(HttpResponse<?> httpResponse) {
    int statusCode = httpResponse.statusCode();
    if (statusCode < 200 || statusCode > 299) {
      if (httpResponse.body() instanceof byte[] body) {
        try {
          JsonNode errorNode = objectMapper.readTree(body).get("error");
          Error error = objectMapper.readValue(errorNode.toString(), Error.class);
          throw new OpenAIException(statusCode, error.message());
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }
      throw new OpenAIException(statusCode, null);
    }
  }

  <T> T deserializeResponse(byte[] response, Class<T> responseClass) {
    try {
      return objectMapper.readValue(response, responseClass);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  JsonNode deserializeResponseAsTree(byte[] response) {
    try {
      return objectMapper.readTree(response);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private String[] getAuthenticationHeaders(String apiKey, Optional<String> organization) {
    List<String> authenticationHeaders = new ArrayList<>();
    authenticationHeaders.add("Authorization");
    authenticationHeaders.add("Bearer " + apiKey);
    organization.ifPresent(
        org -> {
          authenticationHeaders.add("OpenAI-Organization");
          authenticationHeaders.add(org);
        });
    return authenticationHeaders.toArray(new String[] {});
  }

  private record Error(String message, String type) {}
}
