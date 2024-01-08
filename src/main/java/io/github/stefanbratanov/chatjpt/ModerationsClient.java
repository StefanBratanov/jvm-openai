package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Given a input text, outputs if the model classifies it as violating OpenAI's content policy.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/moderations">Moderations</a>
 */
public final class ModerationsClient extends OpenAIClient {

  private final URI endpoint;

  ModerationsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    endpoint = baseUrl.resolve(Endpoint.MODERATIONS.getPath());
  }

  /**
   * Classifies if text violates OpenAI's Content Policy
   *
   * @throws OpenAIException in case of API errors
   */
  public Moderation createModeration(ModerationRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(endpoint)
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Moderation.class);
  }
}
