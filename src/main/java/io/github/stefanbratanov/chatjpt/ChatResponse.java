package io.github.stefanbratanov.chatjpt;

import java.util.List;

public record ChatResponse(
    String id,
    long created,
    String model,
    String systemFingerprint,
    List<Choice> choices,
    Usage usage) {

  public record Choice(int index, Message message, Logprobs logprobs, String finishReason) {

    public record Logprobs(List<Content> content) {

      public record Content(
          String token, double logprob, List<Byte> bytes, List<TopLogprob> topLogprobs) {}

      public record TopLogprob(String token, double logprob, List<Byte> bytes) {}
    }
  }

  public record Usage(int promptTokens, int completionTokens, int totalTokens) {}
}
