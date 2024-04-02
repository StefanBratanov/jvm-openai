package io.github.stefanbratanov.jvm.openai;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
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
      Optional<Duration> requestTimeout) {
    super(apiKey, organization, httpClient, requestTimeout);
  }

  @Override
  HttpRequest.Builder newHttpRequestBuilder(String... headers) {
    return super.newHttpRequestBuilder(headers).header("OpenAI-Beta", "assistants=v1");
  }

  String createQueryParameters(PaginationQueryParameters paginationQueryParameters) {
    return createQueryParameters(paginationQueryParameters, Collections.emptyMap());
  }

  String createQueryParameters(
      PaginationQueryParameters paginationQueryParameters,
      Map<String, Optional<?>> additionalQueryParameters) {
    Map<String, Optional<?>> queryParameters = new HashMap<>();
    queryParameters.put("limit", paginationQueryParameters.limit());
    queryParameters.put("order", paginationQueryParameters.order());
    queryParameters.put("after", paginationQueryParameters.after());
    queryParameters.put("before", paginationQueryParameters.before());
    queryParameters.putAll(additionalQueryParameters);
    return createQueryParameters(queryParameters);
  }
}
