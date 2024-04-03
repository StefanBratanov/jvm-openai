package io.github.stefanbratanov.jvm.openai;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record SubmitToolOutputsRequest(List<ToolOutput> toolOutputs, Optional<Boolean> stream) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<ToolOutput> toolOutputs = new LinkedList<>();

    private Optional<Boolean> stream = Optional.empty();

    /**
     * @param toolOutput Tool output to append to the list of tools for which the outputs are being
     *     submitted.
     */
    public Builder toolOutput(ToolOutput toolOutput) {
      toolOutputs.add(toolOutput);
      return this;
    }

    /**
     * @param toolOutputs Tool outputs to append to the list of tools for which the outputs are
     *     being submitted.
     */
    public Builder toolOutputs(List<ToolOutput> toolOutputs) {
      this.toolOutputs.addAll(toolOutputs);
      return this;
    }

    /**
     * @param stream If true, returns a stream of events that happen during the Run as server-sent
     *     events
     */
    public Builder stream(boolean stream) {
      this.stream = Optional.of(stream);
      return this;
    }

    public SubmitToolOutputsRequest build() {
      return new SubmitToolOutputsRequest(List.copyOf(toolOutputs), stream);
    }
  }

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
