package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Optional;

/**
 * A class which when created using the {@link Builder} can be used to create clients based on the
 * endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API Reference -
 * OpenAI API</a>
 */
public final class ChatJPT {

  private final URI baseUrl;
  private final String apiKey;
  private final Optional<String> organization;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  private ChatJPT(
      URI baseUrl, String apiKey, Optional<String> organization, HttpClient httpClient) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.organization = organization;
    this.httpClient = httpClient;
    this.objectMapper = ChatJPTObjectMapper.getInstance();
  }

  public ChatClient newChatClient() {
    return new ChatClient(baseUrl, apiKey, organization, httpClient, objectMapper);
  }

  public ModelsClient newModelsClient() {
    return new ModelsClient(baseUrl, apiKey, organization, httpClient, objectMapper);
  }

  public static Builder newBuilder(String apiKey) {
    return new Builder(apiKey);
  }

  public static class Builder {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1/";

    private String baseUrl = DEFAULT_BASE_URL;
    private final String apiKey;
    private Optional<String> organization = Optional.empty();

    public Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

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
