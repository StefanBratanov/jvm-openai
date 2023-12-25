package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Optional;

/**
 * A class which when created using the {@link #newBuilder(String)} can be used to create clients
 * based on the endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API
 * Reference - OpenAI API</a>
 */
public final class ChatJPT {

  private final URI baseUrl;
  private final String apiKey;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  private ChatJPT(URI baseUrl, String apiKey, HttpClient httpClient) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.httpClient = httpClient;
    this.objectMapper = ChatJPTObjectMapper.getInstance();
  }

  public ChatClient newChatClient() {
    return new ChatClient(baseUrl, apiKey, Optional.empty(), httpClient, objectMapper);
  }

  /**
   * @param organization for users who belong to multiple organizations specify which organization
   *     will be used for the API requests
   */
  public ChatClient newChatClient(String organization) {
    return new ChatClient(baseUrl, apiKey, Optional.of(organization), httpClient, objectMapper);
  }

  public ModelsClient newModelsClient() {
    return new ModelsClient(baseUrl, apiKey, Optional.empty(), httpClient, objectMapper);
  }

  /**
   * @param organization for users who belong to multiple organizations specify which organization
   *     will be used for the API requests
   */
  public ModelsClient newModelsClient(String organization) {
    return new ModelsClient(baseUrl, apiKey, Optional.of(organization), httpClient, objectMapper);
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

    public ChatJPT build() {
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/";
      }
      HttpClient httpClient = HttpClient.newBuilder().build();
      return new ChatJPT(URI.create(baseUrl), apiKey, httpClient);
    }
  }
}
