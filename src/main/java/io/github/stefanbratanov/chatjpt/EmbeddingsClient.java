package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/** Based on <a href="https://platform.openai.com/docs/api-reference/embeddings">Embeddings</a> */
public final class EmbeddingsClient extends OpenAIClient {

  private final URI endpoint;

  EmbeddingsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    endpoint = baseUrl.resolve(Endpoint.EMBEDDINCS.getPath());
  }

  /**
   * Creates an embedding vector representing the input text.
   *
   * @throws OpenAIException in case of API errors
   */
  public Embeddings createEmbeddings(EmbeddingsRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(endpoint)
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Embeddings.class);
  }
}
