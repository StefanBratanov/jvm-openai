package io.github.stefanbratanov.chatjpt;

import java.util.List;

public record ChatChunkResponse(
    String id, List<Choice> choices, long created, String model, String systemFingerprint) {

  public record Choice(Delta delta, int index, Logprobs logprobs, String finishReason) {

    public record Delta(String role, String content, List<ToolCall> toolCalls) {}
  }
}
