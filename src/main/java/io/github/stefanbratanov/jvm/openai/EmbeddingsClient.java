package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Get a vector representation of a given input that can be easily consumed by machine learning
 * models and algorithms.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/embeddings">Embeddings</a>
 */
public final class EmbeddingsClient extends OpenAIClient {

  private final URI endpoint;

  EmbeddingsClient(
      URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    super(apiKey, organization, httpClient);
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
