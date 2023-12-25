package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/** Based on <a href="https://platform.openai.com/docs/api-reference/models">Models</a> */
public final class ModelsClient extends OpenAIClient {

  private final URI endpoint;

  ModelsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    endpoint = baseUrl.resolve(Endpoint.MODELS.getPath());
  }

  /**
   * Lists the currently available models, and provides basic information about each one such as the
   * owner and availability.
   *
   * @throws OpenAIException in case of API errors
   */
  public List<Model> getModels() {
    HttpRequest httpRequest = newHttpRequestBuilder().uri(endpoint).GET().build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    try {
      JsonNode models = objectMapper.readTree(httpResponse.body());
      return objectMapper.readValue(models.get("data").toString(), new TypeReference<>() {});
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  /**
   * Retrieves a model instance, providing basic information about the model such as the owner and
   * permissioning.
   *
   * @param model The ID of the model to use for this request
   * @throws OpenAIException in case of API errors
   */
  public Model getModel(String model) {
    HttpRequest httpRequest =
        newHttpRequestBuilder().uri(URI.create(endpoint.toString() + "/" + model)).GET().build();
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
        newHttpRequestBuilder().uri(URI.create(endpoint.toString() + "/" + model)).DELETE().build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), DeletionStatus.class);
  }
}
