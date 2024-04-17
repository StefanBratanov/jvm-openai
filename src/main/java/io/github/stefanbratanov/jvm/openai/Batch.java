package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Map;

public record Batch(
    String id,
    String endpoint,
    Errors errors,
    String inputFileId,
    String completionWindow,
    String status,
    String outputFileId,
    String errorFileId,
    long createdAt,
    Long inProgressAt,
    Long expiresAt,
    Long finalizingAt,
    Long completedAt,
    Long failedAt,
    Long expiredAt,
    Long cancellingAt,
    Long cancelledAt,
    RequestCounts requestCounts,
    Map<String, String> metadata) {

  public record Errors(List<Data> data) {
    public record Data(String code, String message, String param, Integer line) {}
  }

  public record RequestCounts(int total, int completed, int failed) {}
}
