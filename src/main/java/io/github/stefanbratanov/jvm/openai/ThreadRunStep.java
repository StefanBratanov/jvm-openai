package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Map;

/** Represents a step in execution of a run. */
public record ThreadRunStep(
    String id,
    long createdAt,
    String assistantId,
    String threadId,
    String runId,
    String type,
    String status,
    StepDetails stepDetails,
    LastError lastError,
    Long expiredAt,
    Long cancelledAt,
    Long failedAt,
    Long completedAt,
    Map<String, String> metadata,
    Usage usage) {

  /** The details of the run step. */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(
        value = StepDetails.MessageCreationStepDetails.class,
        name = Constants.MESSAGE_CREATION_STEP_DETAILS_TYPE),
    @JsonSubTypes.Type(
        value = StepDetails.ToolCallsStepDetails.class,
        name = Constants.TOOL_CALLS_STEP_DETAILS_TYPE),
  })
  public sealed interface StepDetails
      permits StepDetails.MessageCreationStepDetails, StepDetails.ToolCallsStepDetails {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String type();

    record MessageCreationStepDetails(MessageCreation messageCreation) implements StepDetails {
      @Override
      public String type() {
        return Constants.MESSAGE_CREATION_STEP_DETAILS_TYPE;
      }

      public record MessageCreation(String messageId) {}
    }

    record ToolCallsStepDetails(List<ToolCall> toolCalls) implements StepDetails {
      @Override
      public String type() {
        return Constants.TOOL_CALLS_STEP_DETAILS_TYPE;
      }
    }
  }

  /** The last error associated with this run step. */
  public record LastError(String code, String message) {}
}
