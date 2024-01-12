package io.github.stefanbratanov.jvm.openai;

/** An interface which is used to subscribe to streamed partial responses */
public interface ChatStreamResponseSubscriber {

  /** Will be called when a partial response is received */
  void onResponse(ChatChunkResponse response);

  /** Will be called when the stream is complete */
  void onComplete();
}
