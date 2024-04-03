package io.github.stefanbratanov.jvm.openai;

/** An interface which is used to subscribe to streamed partial responses */
public interface ChatCompletionStreamSubscriber {

  /** Will be called when a partial response is received */
  void onChunk(ChatCompletionChunk chunk);

  /** Will be called if any exception happens while processing * */
  void onException(Throwable ex);

  /** Will be called when the stream is complete or an exception has happened */
  void onComplete();
}
