package io.github.stefanbratanov.chatjpt;

import java.util.List;

public record Embeddings(List<Embedding> data, String model, Usage usage) {

  public record Embedding(int index, List<Double> embedding) {}

  public record Usage(int promptTokens, int totalTokens) {}
}
