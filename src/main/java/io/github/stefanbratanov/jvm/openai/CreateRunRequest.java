package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateRunRequest(
    String assistantId,
    Optional<String> model,
    Optional<String> instructions,
    Optional<String> additionalInstructions,
    Optional<List<Tool>> tools,
    Optional<Map<String, String>> metadata,
    Optional<Double> temperature) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String assistantId;

    private Optional<String> model = Optional.empty();
    private Optional<String> instructions = Optional.empty();
    private Optional<String> additionalInstructions = Optional.empty();
    private Optional<List<Tool>> tools = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();
    private Optional<Double> temperature = Optional.empty();

    /**
     * @param assistantId The ID of the assistant to use to execute this run.
     */
    public Builder assistantId(String assistantId) {
      this.assistantId = assistantId;
      return this;
    }

    /**
     * @param model The ID of the Model to be used to execute this run. If a value is provided here,
     *     it will override the model associated with the assistant. If not, the model associated
     *     with the assistant will be used.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    /**
     * @param instructions Overrides the instructions of the assistant. This is useful for modifying
     *     the behavior on a per-run basis.
     */
    public Builder instructions(String instructions) {
      this.instructions = Optional.of(instructions);
      return this;
    }

    /**
     * @param additionalInstructions Appends additional instructions at the end of the instructions
     *     for the run. This is useful for modifying the behavior on a per-run basis without
     *     overriding other instructions.
     */
    public Builder additionalInstructions(String additionalInstructions) {
      this.additionalInstructions = Optional.of(additionalInstructions);
      return this;
    }

    /**
     * @param tools Override the tools the assistant can use for this run. This is useful for
     *     modifying the behavior on a per-run basis.
     */
    public Builder tools(List<Tool> tools) {
      this.tools = Optional.of(tools);
      return this;
    }

    /**
     * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be
     *     useful for storing additional information about the object in a structured format. Keys
     *     can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    /**
     * @param temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8
     *     will make the output more random, while lower values like 0.2 will make it more focused
     *     and deterministic.
     */
    public Builder temperature(Double temperature) {
      this.temperature = Optional.of(temperature);
      return this;
    }

    public CreateRunRequest build() {
      return new CreateRunRequest(
          assistantId, model, instructions, additionalInstructions, tools, metadata, temperature);
    }
  }
}
