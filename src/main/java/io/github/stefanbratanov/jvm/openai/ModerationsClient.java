package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Given some input text, outputs if the model classifies it as potentially harmful across several
 * categories.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/moderations">Moderations</a>
 */
public final class ModerationsClient extends OpenAIClient {

  private final URI endpoint;

  ModerationsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    endpoint = baseUrl.resolve(Endpoint.MODERATIONS.getPath());
  }

  /**
   * Classifies if text is potentially harmful.
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
