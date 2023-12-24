package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.Optional;

class Utils {

  static String[] getAuthenticationHeaders(String apiKey, Optional<String> organization) {
    return organization
        .map(org -> new String[] {"Authorization", "Bearer " + apiKey, "OpenAI-Organization", org})
        .orElse(new String[] {"Authorization", "Bearer " + apiKey});
  }

  static void validateHttpResponse(HttpResponse<byte[]> httpResponse, ObjectMapper objectMapper) {
    if (!isHttpResponseSuccessful(httpResponse)) {
      if (httpResponse.body() == null) {
        throw new OpenAIException(httpResponse.statusCode(), null);
      }
      try {
        Error error = objectMapper.readValue(httpResponse.body(), Error.class);
        throw new OpenAIException(httpResponse.statusCode(), error.message());
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }

  static boolean isHttpResponseSuccessful(HttpResponse<?> httpResponse) {
    return httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300;
  }
}
