package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manage fine-tuning jobs to tailor a model to your specific training data.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/fine-tuning">Fine-tuning</a>
 */
public final class FineTuningClient extends OpenAIClient {

  private final URI baseUrl;

  FineTuningClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Creates a fine-tuning job which begins the process of creating a new model from a given
   * dataset.
   *
   * <p>Response includes details of the enqueued job including job status and the name of the
   * fine-tuned models once complete.
   *
   * @throws OpenAIException in case of API errors
   */
  public FineTuningJob createFineTuningJob(CreateFineTuningJobRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.FINE_TUNING.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), FineTuningJob.class);
  }

  /**
   * List your organization's fine-tuning jobs
   *
   * @param limit Number of fine-tuning jobs to retrieve.
   * @param after Identifier for the last job from the previous pagination request.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedFineTuningJobs listFineTuningJobs(
      Optional<Integer> limit, Optional<String> after) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.FINE_TUNING.getPath() + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedFineTuningJobs.class);
  }

  public record PaginatedFineTuningJobs(List<FineTuningJob> data, boolean hasMore) {
    @JsonIgnore
    public String getLastJobId() {
      return data.get(data.size() - 1).id();
    }
  }

  /**
   * Get status updates for a fine-tuning job.
   *
   * @param fineTuningJobId The ID of the fine-tuning job to get events for.
   * @param limit Number of fine-tuning jobs to retrieve.
   * @param after Identifier for the last job from the previous pagination request.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedFineTuningEvents listFineTuningJobEvents(
      String fineTuningJobId, Optional<Integer> limit, Optional<String> after) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.FINE_TUNING.getPath()
                        + "/"
                        + fineTuningJobId
                        + "/events"
                        + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedFineTuningEvents.class);
  }

  public record PaginatedFineTuningEvents(List<FineTuningJobEvent> data, boolean hasMore) {
    @JsonIgnore
    public String getLastEventId() {
      return data.get(data.size() - 1).id();
    }
  }

  /**
   * List checkpoints for a fine-tuning job.
   *
   * @param fineTuningJobId The ID of the fine-tuning job to get checkpoints for.
   * @param limit Number of checkpoints to retrieve.
   * @param after Identifier for the last checkpoint ID from the previous pagination request.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedFineTuningCheckpoints listFineTuningCheckpoints(
      String fineTuningJobId, Optional<Integer> limit, Optional<String> after) {
    String queryParameters =
        createQueryParameters(
            Map.of(Constants.LIMIT_QUERY_PARAMETER, limit, Constants.AFTER_QUERY_PARAMETER, after));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.FINE_TUNING.getPath()
                        + "/"
                        + fineTuningJobId
                        + "/checkpoints"
                        + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedFineTuningCheckpoints.class);
  }

  public record PaginatedFineTuningCheckpoints(
      List<FineTuningJobCheckpoint> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Get info about a fine-tuning job.
   *
   * @param fineTuningJobId The ID of the fine-tuning job.
   * @throws OpenAIException in case of API errors
   */
  public FineTuningJob retrieveFineTuningJob(String fineTuningJobId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.FINE_TUNING.getPath() + "/" + fineTuningJobId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), FineTuningJob.class);
  }

  /**
   * Immediately cancel a fine-tune job.
   *
   * @param fineTuningJobId The ID of the fine-tuning job to cancel.
   * @throws OpenAIException in case of API errors
   */
  public FineTuningJob cancelFineTuningJob(String fineTuningJobId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(Endpoint.FINE_TUNING.getPath() + "/" + fineTuningJobId + "/cancel"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), FineTuningJob.class);
  }
}
