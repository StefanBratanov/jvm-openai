package io.github.stefanbratanov.jvm.openai;

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
    Long expiresAt,
    Long startedAt,
    Long cancelledAt,
    Long failedAt,
    Long completedAt,
    IncompleteDetails incompleteDetails,
    String model,
    String instructions,
    List<Tool> tools,
    Map<String, String> metadata,
    Usage usage,
    Double temperature,
    Double topP,
    int maxPromptTokens,
    int maxCompletionTokens,
    TruncationStrategy truncationStrategy,
    AssistantsToolChoice toolChoice,
    boolean parallelToolCalls,
    AssistantsResponseFormat responseFormat)
    implements AssistantStreamEvent.Data {

  /** Details on the action required to continue the run. */
  public record RequiredAction(String type, SubmitToolOutputs submitToolOutputs) {
    public record SubmitToolOutputs(List<ToolCall> toolCalls) {}

    public static RequiredAction submitToolOutputsRequiredAction(
        SubmitToolOutputs submitToolOutputs) {
      return new RequiredAction(
          Constants.SUBMIT_TOOL_OUTPUTS_REQUIRED_ACTION_TYPE, submitToolOutputs);
    }
  }

  /** Details on why the run is incomplete. Will be `null` if the run is not incomplete. */
  public record IncompleteDetails(String reason) {}
}
