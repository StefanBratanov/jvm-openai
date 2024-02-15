package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

/**
 * A class which when created using the {@link #newBuilder(String)} can be used to create clients
 * based on the endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API
 * Reference - OpenAI API</a>
 */
public final class OpenAI {

  private final AudioClient audioClient;
  private final ChatClient chatClient;
  private final EmbeddingsClient embeddingsClient;
  private final FineTuningClient fineTuningClient;
  private final FilesClient filesClient;
  private final ImagesClient imagesClient;
  private final ModelsClient modelsClient;
  private final ModerationsClient moderationsClient;
  private final AssistantsClient assistantsClient;
  private final ThreadsClient threadsClient;
  private final MessagesClient messagesClient;
  private final RunsClient runsClient;

  private OpenAI(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    audioClient = new AudioClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    chatClient = new ChatClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    embeddingsClient =
        new EmbeddingsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    fineTuningClient =
        new FineTuningClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    filesClient = new FilesClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    imagesClient = new ImagesClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    modelsClient = new ModelsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    moderationsClient =
        new ModerationsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    assistantsClient =
        new AssistantsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    threadsClient = new ThreadsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    messagesClient = new MessagesClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
    runsClient = new RunsClient(baseUrl, apiKey, organization, httpClient, requestTimeout);
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/audio">Audio</a>
   */
  public AudioClient audioClient() {
    return audioClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/chat">Chat</a>
   */
  public ChatClient chatClient() {
    return chatClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/embeddings">Embeddings</a>
   */
  public EmbeddingsClient embeddingsClient() {
    return embeddingsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/fine-tuning">Fine-tuning</a>
   */
  public FineTuningClient fineTuningClient() {
    return fineTuningClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/files">Files</a>
   */
  public FilesClient filesClient() {
    return filesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/images">Images</a>
   */
  public ImagesClient imagesClient() {
    return imagesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/models">Models</a>
   */
  public ModelsClient modelsClient() {
    return modelsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/moderations">Moderations</a>
   */
  public ModerationsClient moderationsClient() {
    return moderationsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/assistants">Assistants</a>
   */
  public AssistantsClient assistantsClient() {
    return assistantsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/threads">Threads</a>
   */
  public ThreadsClient threadsClient() {
    return threadsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/messages">Messages</a>
   */
  public MessagesClient messagesClient() {
    return messagesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/runs">Runs</a>
   */
  public RunsClient runsClient() {
    return runsClient;
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
    private Optional<HttpClient> httpClient = Optional.empty();
    private Optional<Duration> requestTimeout = Optional.empty();

    public Builder(String apiKey) {
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

    /**
     * @param httpClient a custom {@link HttpClient} which will be used for the API requests
     */
    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = Optional.of(httpClient);
      return this;
    }

    /**
     * @param requestTimeout a timeout in the form of a {@link Duration} which will be set for all
     *     API requests. If none is set, there will be no timeout.
     */
    public Builder requestTimeout(Duration requestTimeout) {
      this.requestTimeout = Optional.of(requestTimeout);
      return this;
    }

    public OpenAI build() {
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/";
      }
      return new OpenAI(
          URI.create(baseUrl),
          apiKey,
          organization,
          httpClient.orElseGet(HttpClient::newHttpClient),
          requestTimeout);
    }
  }
}
