package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/**
 * Represents an execution run on a thread.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/runs">Runs</a>
 */
public final class RunsClient extends OpenAIAssistantsClient {

  private static final String RUNS_SEGMENT = "/runs";
  private static final String STEPS_SEGMENT = "/steps";

  private final URI baseUrl;

  RunsClient(
      URI baseUrl,
      String apiKey,
      Optional<String> organization,
      HttpClient httpClient,
      ObjectMapper objectMapper) {
    super(apiKey, organization, httpClient, objectMapper);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a run.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun createRun(String threadId, CreateRunRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId + RUNS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Create a thread and run it in one request.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun createThreadAndRun(CreateThreadAndRunRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + RUNS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Returns a list of runs belonging to a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadRuns listRuns(String threadId, PaginationQueryParameters queryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + RUNS_SEGMENT
                        + createQueryParameters(queryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadRuns.class);
  }

  public record PaginatedThreadRuns(
      List<ThreadRun> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Returns a list of run steps belonging to a run.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadRunSteps listRunSteps(
      String threadId, String runId, PaginationQueryParameters queryParameters) {
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
                        + createQueryParameters(queryParameters)))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedThreadRunSteps.class);
  }

  public record PaginatedThreadRunSteps(
      List<ThreadRunStep> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Retrieves a run.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun retrieveRun(String threadId, String runId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath() + "/" + threadId + RUNS_SEGMENT + "/" + runId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Retrieves a run step.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRunStep retrieveRunStep(String threadId, String runId, String stepId) {
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
                        + stepId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRunStep.class);
  }

  /**
   * Modifies a run.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun modifyRun(String threadId, String runId, ModifyRunRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath() + "/" + threadId + RUNS_SEGMENT + "/" + runId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * When a run has the status: "requires_action" and required_action.type is submit_tool_outputs,
   * this endpoint can be used to submit the outputs from the tool calls once they're all completed.
   * All outputs must be submitted in a single request.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun submitToolOutputs(
      String threadId, String runId, SubmitToolOutputsRequest request) {
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
                        + "/submit_tool_outputs"))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Cancels a run that is in_progress.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun cancelRun(String threadId, String runId) {
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
                        + "/cancel"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }
}
