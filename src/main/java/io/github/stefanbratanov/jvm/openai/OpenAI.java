package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A class which when created using the {@link OpenAI.Builder} can be used to create clients based
 * on the endpoints defined at <a href="https://platform.openai.com/docs/api-reference">API
 * Reference - OpenAI API</a>
 */
public final class OpenAI {

  private final AudioClient audioClient;
  private final ChatClient chatClient;
  private final EmbeddingsClient embeddingsClient;
  private final FineTuningClient fineTuningClient;
  private final BatchClient batchClient;
  private final FilesClient filesClient;
  private final UploadsClient uploadsClient;
  private final ImagesClient imagesClient;
  private final ModelsClient modelsClient;
  private final ModerationsClient moderationsClient;
  private final AssistantsClient assistantsClient;
  private final ThreadsClient threadsClient;
  private final MessagesClient messagesClient;
  private final RunsClient runsClient;
  private final RunStepsClient runStepsClient;
  private final VectorStoresClient vectorStoresClient;
  private final VectorStoreFilesClient vectorStoreFilesClient;
  private final VectorStoreFileBatchesClient vectorStoreFileBatchesClient;
  private final InvitesClient invitesClient;
  private final UsersClient usersClient;
  private final ProjectsClient projectsClient;
  private final ProjectUsersClient projectUsersClient;
  private final ProjectServiceAccountsClient projectServiceAccountsClient;
  private final ProjectApiKeysClient projectApiKeysClient;
  private final AuditLogsClient auditLogsClient;

  private OpenAI(
      URI baseUrl,
      Optional<String> apiKey,
      Optional<String> adminKey,
      Optional<String> organization,
      Optional<String> project,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    String[] authenticationHeaders = createAuthenticationHeaders(apiKey, organization, project);
    audioClient = new AudioClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    chatClient = new ChatClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    embeddingsClient =
        new EmbeddingsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    fineTuningClient =
        new FineTuningClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    batchClient = new BatchClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    filesClient = new FilesClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    uploadsClient = new UploadsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    imagesClient = new ImagesClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    modelsClient = new ModelsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    moderationsClient =
        new ModerationsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    // Assistants
    assistantsClient =
        new AssistantsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    threadsClient = new ThreadsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    messagesClient = new MessagesClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    runsClient = new RunsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    runStepsClient = new RunStepsClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    vectorStoresClient =
        new VectorStoresClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    vectorStoreFilesClient =
        new VectorStoreFilesClient(baseUrl, authenticationHeaders, httpClient, requestTimeout);
    vectorStoreFileBatchesClient =
        new VectorStoreFileBatchesClient(
            baseUrl, authenticationHeaders, httpClient, requestTimeout);
    // Administration
    String[] adminAuthenticationHeaders = createAdminAuthenticationHeaders(adminKey);
    invitesClient =
        new InvitesClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    usersClient = new UsersClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    projectsClient =
        new ProjectsClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    projectUsersClient =
        new ProjectUsersClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    projectServiceAccountsClient =
        new ProjectServiceAccountsClient(
            baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    projectApiKeysClient =
        new ProjectApiKeysClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
    auditLogsClient =
        new AuditLogsClient(baseUrl, adminAuthenticationHeaders, httpClient, requestTimeout);
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
   *     href="https://platform.openai.com/docs/api-reference/batch">Batch</a>
   */
  public BatchClient batchClient() {
    return batchClient;
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
   *     href="https://platform.openai.com/docs/api-reference/uploads">Uploads</a>
   */
  public UploadsClient uploadsClient() {
    return uploadsClient;
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
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/run-steps">Run Steps</a>
   */
  public RunStepsClient runStepsClient() {
    return runStepsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/vector-stores/create">Vector
   *     Stores</a>
   */
  public VectorStoresClient vectorStoresClient() {
    return vectorStoresClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/vector-stores-files">Vector Store
   *     Files</a>
   */
  public VectorStoreFilesClient vectorStoreFilesClient() {
    return vectorStoreFilesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/vector-stores-file-batches">Vector
   *     Store File Batches</a>
   */
  public VectorStoreFileBatchesClient vectorStoreFileBatchesClient() {
    return vectorStoreFileBatchesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/invite">Invites</a>
   */
  public InvitesClient invitesClient() {
    return invitesClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/users">Users</a>
   */
  public UsersClient usersClient() {
    return usersClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/projects">Projects</a>
   */
  public ProjectsClient projectsClient() {
    return projectsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/project-users">Project Users</a>
   */
  public ProjectUsersClient projectUsersClient() {
    return projectUsersClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/project-service-accounts">Project
   *     Service Accounts</a>
   */
  public ProjectServiceAccountsClient projectServiceAccountsClient() {
    return projectServiceAccountsClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/project-api-keys">Project API Keys</a>
   */
  public ProjectApiKeysClient projectApiKeysClient() {
    return projectApiKeysClient;
  }

  /**
   * @return a client based on <a
   *     href="https://platform.openai.com/docs/api-reference/audit-logs">Audit Logs</a>
   */
  public AuditLogsClient auditLogsClient() {
    return auditLogsClient;
  }

  private String[] createAuthenticationHeaders(
      Optional<String> apiKey, Optional<String> organization, Optional<String> project) {
    List<String> authHeaders = new ArrayList<>();

    apiKey.ifPresent(
        key -> {
          authHeaders.add(Constants.AUTHORIZATION_HEADER);
          authHeaders.add("Bearer " + key);
        });

    organization.ifPresent(
        org -> {
          authHeaders.add(Constants.OPENAI_ORGANIZATION_HEADER);
          authHeaders.add(org);
        });

    project.ifPresent(
        prj -> {
          authHeaders.add(Constants.OPENAI_PROJECT_HEADER);
          authHeaders.add(prj);
        });
    return authHeaders.toArray(new String[] {});
  }

  private String[] createAdminAuthenticationHeaders(Optional<String> adminKey) {
    List<String> authHeaders = new ArrayList<>();
    adminKey.ifPresent(
        key -> {
          authHeaders.add(Constants.AUTHORIZATION_HEADER);
          authHeaders.add("Bearer " + key);
        });
    return authHeaders.toArray(new String[] {});
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * @param apiKey the API key used for authentication
   */
  public static Builder newBuilder(String apiKey) {
    return newBuilder().apiKey(apiKey);
  }

  public static class Builder {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1/";

    private Optional<String> apiKey = Optional.empty();
    private Optional<String> adminKey = Optional.empty();

    private String baseUrl = DEFAULT_BASE_URL;

    private Optional<String> organization = Optional.empty();
    private Optional<String> project = Optional.empty();
    private Optional<HttpClient> httpClient = Optional.empty();
    private Optional<Duration> requestTimeout = Optional.empty();

    public Builder() {}

    /**
     * @param apiKey the API key used for authentication
     */
    public Builder apiKey(String apiKey) {
      this.apiKey = Optional.of(apiKey);
      return this;
    }

    /**
     * @param adminKey the API key used for administration endpoints.
     */
    public Builder adminKey(String adminKey) {
      this.adminKey = Optional.of(adminKey);
      return this;
    }

    /**
     * @param baseUrl the url which exposes the OpenAI API
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * @param organization for users who belong to multiple organizations and are accessing their
     *     projects through their legacy user API key, specify which organization will be used for
     *     the API requests
     */
    public Builder organization(String organization) {
      this.organization = Optional.of(organization);
      return this;
    }

    /**
     * @param project for users who are accessing their projects through their legacy user API key,
     *     specify which project will be used for the API requests
     */
    public Builder project(String project) {
      this.project = Optional.of(project);
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
     * @param requestTimeout a timeout in the form of a {@link Duration} which will be set for the
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
          adminKey,
          organization,
          project,
          httpClient.orElseGet(HttpClient::newHttpClient),
          requestTimeout);
    }
  }
}
