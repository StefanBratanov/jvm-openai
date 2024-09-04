package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the steps (model and tool calls) taken during the run.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/run-steps">Run Steps</a>
 */
public final class RunStepsClient extends OpenAIAssistantsClient {

  private static final String RUNS_SEGMENT = "/runs";
  private static final String STEPS_SEGMENT = "/steps";

  private final URI baseUrl;

  RunStepsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of run steps belonging to a run.
   *
   * @param include A list of additional fields to include in the response.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadRunSteps listRunSteps(
      String threadId,
      String runId,
      PaginationQueryParameters paginationQueryParameters,
      Optional<List<String>> include) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + RUNS_SEGMENT
                        + "/"
                        + runId
                        + STEPS_SEGMENT
                        + createQueryParameters(
                            paginationQueryParameters,
                            Map.of(Constants.INCLUDE_QUERY_PARAMETER, include))))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadRunSteps.class);
  }

  public record PaginatedThreadRunSteps(
      List<ThreadRunStep> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves a run step.
   *
   * @param include A list of additional fields to include in the response.
   * @throws OpenAIException in case of API errors
   */
  public ThreadRunStep retrieveRunStep(
      String threadId, String runId, String stepId, Optional<List<String>> include) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + RUNS_SEGMENT
                        + "/"
                        + runId
                        + STEPS_SEGMENT
                        + "/"
                        + stepId
                        + createQueryParameters(
                            Map.of(Constants.INCLUDE_QUERY_PARAMETER, include))))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRunStep.class);
  }
}
