package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an execution run on a thread.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/runs">Runs</a>
 */
public final class RunsClient extends OpenAIAssistantsClient {

  private static final String RUNS_SEGMENT = "/runs";

  private final URI baseUrl;

  RunsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Create a run.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun createRun(String threadId, CreateRunRequest request) {
    HttpRequest httpRequest = createRunPostRequest(threadId, request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Create a run and stream the result of executing it.
   *
   * @throws OpenAIException in case of API errors
   */
  public Stream<AssistantStreamEvent> createRunAndStream(
      String threadId, CreateRunRequest request) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createRunPostRequest(threadId, request);
    return getAssistantStreamEvents(httpRequest);
  }

  /**
   * Same as {@link #createRunAndStream(String, CreateRunRequest)} but can pass a {@link
   * AssistantStreamEventSubscriber} implementation instead of using a {@link
   * Stream<AssistantStreamEvent>}
   *
   * @throws OpenAIException in case of API errors
   */
  public void createRunAndStream(
      String threadId, CreateRunRequest request, AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createRunPostRequest(threadId, request);
    streamAndHandleAssistantEvents(httpRequest, subscriber);
  }

  /**
   * Create a thread and run it in one request.
   *
   * @throws OpenAIException in case of API errors
   */
  public ThreadRun createThreadAndRun(CreateThreadAndRunRequest request) {
    HttpRequest httpRequest = createThreadAndRunPostRequest(request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Create a thread and run it in one request and stream the result of executing it.
   *
   * @throws OpenAIException in case of API errors
   */
  public Stream<AssistantStreamEvent> createThreadAndRunAndStream(
      CreateThreadAndRunRequest request) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createThreadAndRunPostRequest(request);
    return getAssistantStreamEvents(httpRequest);
  }

  /**
   * Same as {@link #createThreadAndRunAndStream(CreateThreadAndRunRequest)} but can pass a {@link
   * AssistantStreamEventSubscriber} implementation instead of using a {@link
   * Stream<AssistantStreamEvent>}
   *
   * @throws OpenAIException in case of API errors
   */
  public void createThreadAndRunAndStream(
      CreateThreadAndRunRequest request, AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createThreadAndRunPostRequest(request);
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
    HttpRequest httpRequest = createSubmitToolOutputsPostRequest(threadId, runId, request);
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), ThreadRun.class);
  }

  /**
   * Same as {@link #submitToolOutputs(String, String, SubmitToolOutputsRequest)} but the result of
   * executing the thread run will be streamed
   *
   * @throws OpenAIException in case of API errors
   */
  public Stream<AssistantStreamEvent> submitToolOutputsAndStream(
      String threadId, String runId, SubmitToolOutputsRequest request) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createSubmitToolOutputsPostRequest(threadId, runId, request);
    return getAssistantStreamEvents(httpRequest);
  }

  /**
   * Same as {@link #submitToolOutputsAndStream(String, String, SubmitToolOutputsRequest)} but can
   * pass a {@link AssistantStreamEventSubscriber} implementation instead of using a {@link
   * Stream<AssistantStreamEvent>}
   *
   * @throws OpenAIException in case of API errors
   */
  public void submitToolOutputsAndStream(
      String threadId,
      String runId,
      SubmitToolOutputsRequest request,
      AssistantStreamEventSubscriber subscriber) {
    validateStreamRequest(request::stream);
    HttpRequest httpRequest = createSubmitToolOutputsPostRequest(threadId, runId, request);
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

  private HttpRequest createRunPostRequest(String threadId, CreateRunRequest request) {
    return newHttpRequestBuilder()
        .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + "/" + threadId + RUNS_SEGMENT))
        .POST(createBodyPublisher(request))
        .build();
  }

  private HttpRequest createThreadAndRunPostRequest(CreateThreadAndRunRequest request) {
    return newHttpRequestBuilder()
        .uri(baseUrl.resolve(Endpoint.THREADS.getPath() + RUNS_SEGMENT))
        .POST(createBodyPublisher(request))
        .build();
  }

  private HttpRequest createSubmitToolOutputsPostRequest(
      String threadId, String runId, SubmitToolOutputsRequest request) {
    return newHttpRequestBuilder()
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
  }

  private record RawAssistantStreamEvent(String event, String data) {}

  private Stream<AssistantStreamEvent> getAssistantStreamEvents(HttpRequest httpRequest) {
    return streamRawAssistantEvents(httpRequest)
        .map(
            rawAssistantStreamEvent -> {
              String event = rawAssistantStreamEvent.event;
              String rawData = rawAssistantStreamEvent.data;
              AssistantStreamEvent.Data data = null;
              if (event.startsWith("thread.run.step.delta")) {
                data = deserializeData(rawData, ThreadRunStepDelta.class);
              } else if (event.startsWith("thread.run.step")) {
                data = deserializeData(rawData, ThreadRunStep.class);
              } else if (event.startsWith("thread.run")) {
                data = deserializeData(rawData, ThreadRun.class);
              } else if (event.startsWith("thread.message.delta")) {
                data = deserializeData(rawData, ThreadMessageDelta.class);
              } else if (event.startsWith("thread.message")) {
                data = deserializeData(rawData, ThreadMessage.class);
              } else if (event.startsWith("thread")) {
                data = deserializeData(rawData, Thread.class);
              }
              return new AssistantStreamEvent(event, data);
            });
  }

  private void streamAndHandleAssistantEvents(
      HttpRequest httpRequest, AssistantStreamEventSubscriber subscriber) {
    CompletableFuture.supplyAsync(() -> streamRawAssistantEvents(httpRequest))
        .thenAccept(
            rawAssistantStreamEvents ->
                rawAssistantStreamEvents.forEach(
                    rawAssistantStreamEvent ->
                        handleRawAssistantStreamEvent(rawAssistantStreamEvent, subscriber)))
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                subscriber.onException(ex);
              }
              subscriber.onComplete();
            });
  }

  private Stream<RawAssistantStreamEvent> streamRawAssistantEvents(HttpRequest httpRequest) {
    Stream<String> sseEvents = streamServerSentEvents(httpRequest);
    return StreamSupport.stream(new RawAssistantStreamEventSpliterator(sseEvents), false);
  }

  private void handleRawAssistantStreamEvent(
      RawAssistantStreamEvent rawAssistantStreamEvent, AssistantStreamEventSubscriber subscriber) {
    String event = rawAssistantStreamEvent.event;
    String data = rawAssistantStreamEvent.data;
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

  private static class RawAssistantStreamEventSpliterator
      implements Spliterator<RawAssistantStreamEvent> {

    private final Iterator<String> sseEventsIterator;

    RawAssistantStreamEventSpliterator(Stream<String> sseEvents) {
      this.sseEventsIterator = sseEvents.iterator();
    }

    @Override
    public boolean tryAdvance(Consumer<? super RawAssistantStreamEvent> action) {
      String event = getNextValue();
      if (event == null) {
        return false;
      }
      String data = getNextValue();
      if (data == null) {
        return false;
      }
      action.accept(new RawAssistantStreamEvent(event, data));
      return true;
    }

    @Override
    public Spliterator<RawAssistantStreamEvent> trySplit() {
      return null;
    }

    @Override
    public long estimateSize() {
      return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
      return ORDERED | NONNULL;
    }

    private String getNextValue() {
      if (!sseEventsIterator.hasNext()) {
        return null;
      }
      return sseEventsIterator.next().split(":", 2)[1].trim();
    }
  }
}
