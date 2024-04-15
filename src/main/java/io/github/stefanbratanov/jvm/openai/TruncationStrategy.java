package io.github.stefanbratanov.jvm.openai;

/**
 * Thread Truncation Controls
 *
 * @param type The truncation strategy to use for the thread.
 * @param lastMessages The number of most recent messages from the thread when constructing the
 *     context for the run.
 */
public record TruncationStrategy(String type, Integer lastMessages) {

  public static TruncationStrategy auto() {
    return new TruncationStrategy("auto", null);
  }

  public static TruncationStrategy lastMessages(int lastMessages) {
    return new TruncationStrategy("last_messages", lastMessages);
  }
}
