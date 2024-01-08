package io.github.stefanbratanov.chatjpt;

import java.util.List;
import java.util.Map;

/** Represents an execution run on a thread. */
public record ThreadRun(
    String id,
    long createdAt,
    String threadId,
    String assistantId,
    String status,
    RequiredAction requiredAction,
    LastError lastError,
    long expiresAt,
    Long startedAt,
    Long cancelledAt,
    Long failedAt,
    Long completedAt,
    String model,
    String instructions,
    List<Tool> tools,
    List<String> fileIds,
    Map<String, String> metadata) {

  public record RequiredAction(String type, SubmitToolOutputs submitToolOutputs) {
    public record SubmitToolOutputs(List<ToolCall> toolCalls) {}
  }

  public record LastError(String code, String message) {}
}
