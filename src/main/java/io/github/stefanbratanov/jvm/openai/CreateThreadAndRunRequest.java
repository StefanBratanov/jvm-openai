package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateThreadAndRunRequest(
    String assistantId,
    Optional<CreateThreadRequest> thread,
    Optional<String> model,
    Optional<String> instructions,
    Optional<List<Tool>> tools,
    Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String assistantId;

    private Optional<CreateThreadRequest> thread = Optional.empty();
    private Optional<String> model = Optional.empty();
    private Optional<String> instructions = Optional.empty();
    private Optional<List<Tool>> tools = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param assistantId The ID of the assistant to use to execute this run.
     */
    public Builder assistantId(String assistantId) {
      this.assistantId = assistantId;
      return this;
    }

    /**
     * @param thread Thread to be created as part of the request
     */
    public Builder thread(CreateThreadRequest thread) {
      this.thread = Optional.of(thread);
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

    public CreateThreadAndRunRequest build() {
      if (assistantId == null) {
        throw new IllegalStateException("assistantId must be set");
      }
      return new CreateThreadAndRunRequest(
          assistantId, thread, model, instructions, tools, metadata);
    }
  }
}
