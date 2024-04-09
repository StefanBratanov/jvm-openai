package io.github.stefanbratanov.jvm.openai;

/** An interface which is used to subscribe to assistant events emitted when streaming a Run. */
public interface AssistantStreamEventSubscriber {

  /** Will be called when the event data is {@link Thread} */
  void onThread(String event, Thread thread);

  /** Will be called when the event data is {@link ThreadRun} */
  void onThreadRun(String event, ThreadRun threadRun);

  /** Will be called when the event data is {@link ThreadRunStep} */
  void onThreadRunStep(String event, ThreadRunStep threadRunStep);

  /** Will be called when the event data is {@link ThreadRunStepDelta} */
  void onThreadRunStepDelta(String event, ThreadRunStepDelta threadRunStepDelta);

  /** Will be called when the event data is {@link ThreadMessage} */
  void onThreadMessage(String event, ThreadMessage threadMessage);

  /** Will be called when the event data is {@link ThreadMessageDelta} */
  void onThreadMessageDelta(String event, ThreadMessageDelta threadMessageDelta);

  /**
   * Will be called when an unsupported assistant event is emitted.
   *
   * @param data the raw data which was emitted
   */
  void onUnknownEvent(String event, String data);

  /** Will be called if any exception happens while processing */
  void onException(Throwable ex);

  /** Will be called when the stream is complete or an exception has happened */
  void onComplete();
}
