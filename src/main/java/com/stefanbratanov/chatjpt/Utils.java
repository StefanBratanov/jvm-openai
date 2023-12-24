package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;

class Utils {

  static String[] getAuthorizationHeader(String apiKey) {
    return new String[] {"Authorization", "Bearer " + apiKey};
  }

  static void validateHttpResponse(HttpResponse<byte[]> httpResponse, ObjectMapper objectMapper) {
    int statusCode = httpResponse.statusCode();
    if (statusCode < 200 || statusCode > 299) {
      if (httpResponse.body() == null) {
        throw new OpenAIException(statusCode, null);
      }
      try {
        Error error = objectMapper.readValue(httpResponse.body(), Error.class);
        throw new OpenAIException(statusCode, error.message());
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }
}
