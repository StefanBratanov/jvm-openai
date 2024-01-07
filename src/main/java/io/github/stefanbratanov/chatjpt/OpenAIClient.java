package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  String createQueryParameters(Map<String, Optional<?>> queryParameters) {
    return queryParameters.entrySet().stream()
        .filter(entry -> entry.getValue().isPresent())
        .map(entry -> entry.getKey() + "=" + entry.getValue().get())
        .collect(Collectors.joining("&", "?", ""));
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
      java.lang.Thread.currentThread().interrupt();
      throw new RuntimeException("Operation was interrupted", ex);
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
      getErrorMessageFromHttpResponse(httpResponse)
          .ifPresentOrElse(
              errorMessage -> {
                throw new OpenAIException(statusCode, errorMessage);
              },
              () -> {
                throw new OpenAIException(statusCode, null);
              });
    }
  }

  <T> T deserializeResponse(byte[] response, Class<T> responseClass) {
    try {
      return objectMapper.readValue(response, responseClass);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  <T> List<T> deserializeDataInResponseAsList(byte[] response, Class<T> elementType) {
    try {
      JsonNode responseNode = objectMapper.readTree(response);
      return objectMapper.readValue(
          responseNode.get("data").traverse(),
          objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
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
    List<String> authHeaders = new ArrayList<>();
    authHeaders.add("Authorization");
    authHeaders.add("Bearer " + apiKey);
    organization.ifPresent(
        org -> {
          authHeaders.add("OpenAI-Organization");
          authHeaders.add(org);
        });
    return authHeaders.toArray(new String[] {});
  }

  private Optional<String> getErrorMessageFromHttpResponse(HttpResponse<?> httpResponse) {
    try {
      byte[] body;
      if (httpResponse.body() instanceof byte[]) {
        body = (byte[]) httpResponse.body();
      } else if (httpResponse.body() instanceof Path path) {
        body = Files.readAllBytes(path);
      } else if (httpResponse.body() instanceof Stream<?> stream) {
        body = stream.map(elem -> (String) elem).collect(Collectors.joining()).getBytes();
      } else {
        return Optional.empty();
      }
      return Optional.ofNullable(objectMapper.readTree(body).get("error"))
          .flatMap(
              errorNode ->
                  Optional.ofNullable(errorNode.get("message"))
                      .filter(node -> !node.asText().isBlank())
                      // fallback to "type" if no "message"
                      .or(() -> Optional.ofNullable(errorNode.get("type"))))
          .map(JsonNode::asText);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
