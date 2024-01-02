package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Optional;

/**
 * A class which when created using the {@link #newBuilder(String)} can be used to create clients
 * based on the endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API
 * Reference - OpenAI API</a>
 */
public final class ChatJPT {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.registerModule(new Jdk8Module());
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }

  private final ChatClient chatClient;
  private final ModelsClient modelsClient;
  private final AudioClient audioClient;
  private final ImagesClient imagesClient;
  private final ModerationsClient moderationsClient;
  private final EmbeddingsClient embeddingsClient;
  private final FilesClient filesClient;
  private final FineTuningClient fineTuningClient;

  private ChatJPT(
      URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    chatClient = new ChatClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    modelsClient = new ModelsClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    audioClient = new AudioClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    imagesClient = new ImagesClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    moderationsClient =
        new ModerationsClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    embeddingsClient =
        new EmbeddingsClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    filesClient = new FilesClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
    fineTuningClient =
        new FineTuningClient(baseUrl, apiKey, organization, httpClient, OBJECT_MAPPER);
  }

  public ChatClient chatClient() {
    return chatClient;
  }

  public ModelsClient modelsClient() {
    return modelsClient;
  }

  public AudioClient audioClient() {
    return audioClient;
  }

  public ImagesClient imagesClient() {
    return imagesClient;
  }

  public ModerationsClient moderationsClient() {
    return moderationsClient;
  }

  public EmbeddingsClient embeddingsClient() {
    return embeddingsClient;
  }

  public FilesClient filesClient() {
    return filesClient;
  }

  public FineTuningClient fineTuningClient() {
    return fineTuningClient;
  }

  /**
   * @param apiKey the API key used for authentication
   */
  public static Builder newBuilder(String apiKey) {
    return new Builder(apiKey);
  }

  public static class Builder {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1/";

    private final String apiKey;

    private String baseUrl = DEFAULT_BASE_URL;

    private Optional<String> organization = Optional.empty();

    Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    /**
     * @param baseUrl the url which exposes the OpenAI API
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * @param organization for users who belong to multiple organizations specify which organization
     *     will be used for the API requests
     */
    public Builder organization(String organization) {
      this.organization = Optional.of(organization);
      return this;
    }

    public ChatJPT build() {
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/";
      }
      HttpClient httpClient = HttpClient.newBuilder().build();
      return new ChatJPT(URI.create(baseUrl), apiKey, organization, httpClient);
    }
  }
}
