package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Optional;

public record SubmitToolOutputsRequest(List<ToolOutput> toolOutputs) {

  public record ToolOutput(Optional<String> toolCallId, Optional<String> output) {

    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {

      private Optional<String> toolCallId = Optional.empty();
      private Optional<String> output = Optional.empty();

      /**
       * @param toolCallId The ID of the tool call in the required_action object within the run
       *     object the output is being submitted for.
       */
      public Builder toolCallId(String toolCallId) {
        this.toolCallId = Optional.of(toolCallId);
        return this;
      }

      /**
       * @param output The output of the tool call to be submitted to continue the run.
       */
      public Builder output(String output) {
        this.output = Optional.of(output);
        return this;
      }

      public ToolOutput build() {
        return new ToolOutput(toolCallId, output);
      }
    }
  }
}
