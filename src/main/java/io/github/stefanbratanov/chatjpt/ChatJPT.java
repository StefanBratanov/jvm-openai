package io.github.stefanbratanov.chatjpt;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Optional;

/**
 * A class which when created using the {@link #newBuilder(String)} can be used to create clients
 * based on the endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API
 * Reference - OpenAI API</a>
 */
public final class ChatJPT {

  private final ChatClient chatClient;
  private final ModelsClient modelsClient;

  private ChatJPT(
      URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    this.chatClient =
        new ChatClient(
            baseUrl, apiKey, organization, httpClient, ChatJPTObjectMapper.getInstance());
    this.modelsClient =
        new ModelsClient(
            baseUrl, apiKey, organization, httpClient, ChatJPTObjectMapper.getInstance());
  }

  public ChatClient chatClient() {
    return chatClient;
  }

  public ModelsClient modelsClient() {
    return modelsClient;
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
