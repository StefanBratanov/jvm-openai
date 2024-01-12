package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/**
 * List and describe the various models available in the API. You can refer to the Models
 * documentation to understand what models are available and the differences between them.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/models">Models</a>
 */
public final class ModelsClient extends OpenAIClient {

  private final URI baseUrl;

  ModelsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    this.baseUrl = baseUrl;
  }

  /**
   * Lists the currently available models, and provides basic information about each one such as the
   * owner and availability.
   *
   * @throws OpenAIException in case of API errors
   */
  public List<Model> listModels() {
    HttpRequest httpRequest =
        newHttpRequestBuilder().uri(baseUrl.resolve(Endpoint.MODELS.getPath())).GET().build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeDataInResponseAsList(httpResponse.body(), Model.class);
  }

  /**
   * Retrieves a model instance, providing basic information about the model such as the owner and
   * permissioning.
   *
   * @param model The ID of the model to use for this request
   * @throws OpenAIException in case of API errors
   */
  public Model retrieveModel(String model) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.MODELS.getPath() + "/" + model))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Model.class);
  }

  /**
   * Delete a fine-tuned model. You must have the Owner role in your organization to delete a model.
   *
   * @param model The model to delete
   * @throws OpenAIException in case of API errors
   */
  public DeletionStatus deleteModel(String model) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.MODELS.getPath() + "/" + model))
            .DELETE()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
