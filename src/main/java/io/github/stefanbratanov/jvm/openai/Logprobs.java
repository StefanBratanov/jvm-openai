package io.github.stefanbratanov.jvm.openai;

import java.util.List;

/** Log probability information */
public record Logprobs(List<Content> content, List<Refusal> refusal) {

  public record Content(
      String token, double logprob, List<Byte> bytes, List<TopLogprob> topLogprobs) {}

  public record Refusal(
      String token, double logprob, List<Byte> bytes, List<TopLogprob> topLogprobs) {}

  public record TopLogprob(String token, double logprob, List<Byte> bytes) {}
}
