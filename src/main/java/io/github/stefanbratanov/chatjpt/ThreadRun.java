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

  /** Details on the action required to continue the run. */
  public record RequiredAction(String type, SubmitToolOutputs submitToolOutputs) {
    public record SubmitToolOutputs(List<ToolCall> toolCalls) {}
  }

  /** The last error associated with this run. */
  public record LastError(String code, String message) {}
}
