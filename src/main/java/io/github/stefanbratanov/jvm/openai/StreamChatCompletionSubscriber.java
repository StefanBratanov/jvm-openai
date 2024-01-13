package io.github.stefanbratanov.jvm.openai;

/** An interface which is used to subscribe to streamed partial responses */
public interface StreamChatCompletionSubscriber {

  /** Will be called when a partial response is received */
  void onChunk(ChatCompletionChunk chunk);

  /** Will be called when the stream is complete */
  void onComplete();
}
