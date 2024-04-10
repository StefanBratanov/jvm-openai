package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/** Represents a run step delta i.e. any changed fields on a run step during streaming.. */
public record ThreadRunStepDelta(String id, Delta delta) implements AssistantStreamEvent.Data {

  /** The delta containing the fields that have changed on the run step. * */
  public record Delta(StepDetails stepDetails) {}

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

    record ToolCallsStepDetails(List<DeltaToolCall> toolCalls) implements StepDetails {
      @Override
      public String type() {
        return Constants.TOOL_CALLS_STEP_DETAILS_TYPE;
      }
    }
  }
}
