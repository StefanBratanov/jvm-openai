package io.github.stefanbratanov.jvm.openai;

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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Subclasses should be based on one of the endpoints defined at <a
 * href="https://platform.openai.com/docs/api-reference">API Reference - OpenAI API</a>
 */
abstract class OpenAIClient {

  private static final String STREAM_TERMINATION_REGEX = "(data: \\[DONE]|event: done)";

  private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

  private final String[] authenticationHeaders;
  private final HttpClient httpClient;
  private final Optional<Duration> requestTimeout;

  OpenAIClient(
      String[] authenticationHeaders, HttpClient httpClient, Optional<Duration> requestTimeout) {
    this.authenticationHeaders = authenticationHeaders;
    this.httpClient = httpClient;
    this.requestTimeout = requestTimeout;
  }

  HttpRequest.Builder newHttpRequestBuilder(String... headers) {
    HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();

    if (authenticationHeaders.length > 0) {
      httpRequestBuilder.headers(authenticationHeaders);
    }
    if (headers.length > 0) {
      httpRequestBuilder.headers(headers);
    }
    requestTimeout.ifPresent(httpRequestBuilder::timeout);
    return httpRequestBuilder;
  }

  String createQueryParameters(Map<String, Optional<?>> queryParameters) {
    return queryParameters.entrySet().stream()
        .filter(entry -> entry.getValue().isPresent())
        .flatMap(
            entry -> {
              Object value = entry.getValue().get();
              if (value instanceof Collection<?> items) {
                return items.stream().map(item -> entry.getKey() + "=" + item);
              } else {
                return Stream.of(entry.getKey() + "=" + value);
              }
            })
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

  Stream<String> streamServerSentEvents(HttpRequest httpRequest) {
    return sendHttpRequest(httpRequest, HttpResponse.BodyHandlers.ofLines())
        .body()
        .filter(sseEvent -> !sseEvent.isBlank())
        .takeWhile(sseEvent -> !sseEvent.matches(STREAM_TERMINATION_REGEX));
  }

  void validateStreamRequest(Supplier<Optional<Boolean>> streamField) {
    if (!streamField.get().orElse(false)) {
      throw new IllegalArgumentException("stream must be set to true when requesting a stream");
    }
  }

  void validateHttpResponse(HttpResponse<?> httpResponse) {
    int statusCode = httpResponse.statusCode();
    if (statusCode < 200 || statusCode > 299) {
      getErrorFromHttpResponse(httpResponse)
          .ifPresentOrElse(
              error -> {
                throw new OpenAIException(statusCode, error);
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

  <T> T deserializeData(String data, Class<T> responseClass) {
    try {
      return objectMapper.readValue(data, responseClass);
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

  private Optional<OpenAIException.Error> getErrorFromHttpResponse(HttpResponse<?> httpResponse) {
    return getErrorBodyFromHttpResponse(httpResponse)
        .flatMap(
            body -> {
              try {
                JsonNode errorNode = objectMapper.readTree(body).get("error");
                if (errorNode == null) {
                  return Optional.empty();
                }
                return Optional.of(
                    objectMapper.treeToValue(errorNode, OpenAIException.Error.class));
              } catch (JsonProcessingException ex) {
                return Optional.empty();
              } catch (IOException ex) {
                throw new UncheckedIOException(ex);
              }
            });
  }

  private Optional<byte[]> getErrorBodyFromHttpResponse(HttpResponse<?> httpResponse) {
    byte[] body;
    if (httpResponse.body() instanceof byte[]) {
      body = (byte[]) httpResponse.body();
    } else if (httpResponse.body() instanceof Path path) {
      try {
        body = Files.readAllBytes(path);
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    } else if (httpResponse.body() instanceof Stream<?> stream) {
      body = stream.map(String.class::cast).collect(Collectors.joining()).getBytes();
    } else {
      return Optional.empty();
    }
    return Optional.of(body);
  }
}
