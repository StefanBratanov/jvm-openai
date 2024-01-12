package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Optional;

/**
 * Subclasses should be based on the <a
 * href="https://platform.openai.com/docs/assistants/overview">Assistants API</a>
 */
class OpenAIAssistantsClient extends OpenAIClient {

  OpenAIAssistantsClient(
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
  }

  @Override
  HttpRequest.Builder newHttpRequestBuilder(String... headers) {
    return super.newHttpRequestBuilder(headers).header("OpenAI-Beta", "assistants=v1");
  }

  String createQueryParameters(PaginationQueryParameters queryParameters) {
    return createQueryParameters(
        Map.of(
            "limit",
            queryParameters.limit(),
            "order",
            queryParameters.order(),
            "after",
            queryParameters.after(),
            "before",
            queryParameters.before()));
  }
}
