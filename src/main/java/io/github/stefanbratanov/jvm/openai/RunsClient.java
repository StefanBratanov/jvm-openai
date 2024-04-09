package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
      Optional<Duration> requestTimeout) {
    super(apiKey, organization, httpClient, requestTimeout);
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
   * Create a run and stream the result of executing it.
   *
   * @param subscriber an implementation of {@link AssistantStreamEventSubscriber} which will handle
   *     the incoming events
   * @throws OpenAIException in case of API errors
   */
  public void createRunAndStream(
      String threadId, CreateRunRequest request, AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId + RUNS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    streamAndHandleAssistantEvents(httpRequest, subscriber);
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
   * Create a thread and run it in one request and stream the result of executing it.
   *
   * @param subscriber an implementation of {@link AssistantStreamEventSubscriber} which will handle
   *     the incoming events
   * @throws OpenAIException in case of API errors
   */
  public void createThreadAndRunAndStream(
      CreateThreadAndRunRequest request, AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + RUNS_SEGMENT))
            .POST(createBodyPublisher(request))
            .build();
    streamAndHandleAssistantEvents(httpRequest, subscriber);
  }

  /**
   * Returns a list of runs belonging to a thread.
   *
   * @throws OpenAIException in case of API errors
   */
  public PaginatedThreadRuns listRuns(
      String threadId, PaginationQueryParameters paginationQueryParameters) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(
                baseUrl.resolve(
                    Endpoint.THREADS.getPath()
                        + "/"
                        + threadId
                        + RUNS_SEGMENT
                        + createQueryParameters(paginationQueryParameters)))
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
      String threadId, String runId, PaginationQueryParameters paginationQueryParameters) {
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
                        + createQueryParameters(paginationQueryParameters)))
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
   * Same as {@link #submitToolOutputs(String, String, SubmitToolOutputsRequest)} but the result of
   * executing the thread run will be streamed
   *
   * @param subscriber an implementation of {@link AssistantStreamEventSubscriber} which will handle
   *     the incoming events
   * @throws OpenAIException in case of API errors
   */
  public void submitToolOutputsAndStream(
      String threadId,
      String runId,
      SubmitToolOutputsRequest request,
      AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
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
    streamAndHandleAssistantEvents(httpRequest, subscriber);
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

  private void streamAndHandleAssistantEvents(
      HttpRequest httpRequest, AssistantStreamEventSubscriber subscriber) {
    CompletableFuture.supplyAsync(() -> streamServerSentEvents(httpRequest))
        .thenAccept(sseEvents -> handleAssistantSseEvents(sseEvents, subscriber))
        .whenComplete((result, ex) -> handleAssistantEventsStreamCompletion(ex, subscriber));
  }

  private void handleAssistantSseEvents(
      Stream<String> sseEvents, AssistantStreamEventSubscriber subscriber) {
    Iterator<String> iterator = sseEvents.iterator();
    while (iterator.hasNext()) {
      // have to group the event and the data because they are received separately
      String event = iterator.next().split(":", 2)[1].trim();
      String data;
      if (iterator.hasNext()) {
        data = iterator.next().split(":", 2)[1].trim();
      } else {
        throw new IllegalStateException("No data available for event " + event);
      }
      handleAssistantSseEvent(event, data, subscriber);
    }
  }

  private void handleAssistantSseEvent(
      String event, String data, AssistantStreamEventSubscriber subscriber) {
    if (event.startsWith("thread.run.step.delta")) {
      subscriber.onThreadRunStepDelta(event, deserializeData(data, ThreadRunStepDelta.class));
    } else if (event.startsWith("thread.run.step")) {
      subscriber.onThreadRunStep(event, deserializeData(data, ThreadRunStep.class));
    } else if (event.startsWith("thread.run")) {
      subscriber.onThreadRun(event, deserializeData(data, ThreadRun.class));
    } else if (event.startsWith("thread.message.delta")) {
      subscriber.onThreadMessageDelta(event, deserializeData(data, ThreadMessageDelta.class));
    } else if (event.startsWith("thread.message")) {
      subscriber.onThreadMessage(event, deserializeData(data, ThreadMessage.class));
    } else if (event.startsWith("thread")) {
      subscriber.onThread(event, deserializeData(data, Thread.class));
    } else {
      subscriber.onUnknownEvent(event, data);
    }
  }

  private void handleAssistantEventsStreamCompletion(
      Throwable ex, AssistantStreamEventSubscriber subscriber) {
    if (ex != null) {
      subscriber.onException(ex);
    }
    subscriber.onComplete();
  }
}
