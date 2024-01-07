package io.github.stefanbratanov.chatjpt;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateAssistantRequest(
    String model,
    Optional<String> name,
    Optional<String> description,
    Optional<String> instructions,
    Optional<List<Tool>> tools,
    Optional<List<String>> fileIds,
    Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = "gpt-4";

    private String model = DEFAULT_MODEL;

    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<String> instructions = Optional.empty();

    private final List<Tool> tools = new LinkedList<>();

    private Optional<List<String>> fileIds = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param model ID of the model to use.
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param name The name of the assistant. The maximum length is 256 characters.
     */
    public Builder name(String name) {
      this.name = Optional.of(name);
      return this;
    }

    /**
     * @param description The description of the assistant. The maximum length is 512 characters.
     */
    public Builder description(String description) {
      this.description = Optional.of(description);
      return this;
    }

    /**
     * @param instructions The system instructions that the assistant uses. The maximum length is
     *     32768 characters.
     */
    public Builder instructions(String instructions) {
      this.instructions = Optional.of(instructions);
      return this;
    }

    /**
     * @param tool tool to append to the list of tools enabled on the assistant.
     */
    public Builder tool(Tool tool) {
      tools.add(tool);
      return this;
    }

    /**
     * @param tools tools to append to the list of tools enabled on the assistant.
     */
    public Builder tools(List<Tool> tools) {
      this.tools.addAll(tools);
      return this;
    }

    /**
     * @param fileIds A list of file IDs attached to this assistant. There can be a maximum of 20
     *     files attached to the assistant. Files are ordered by their creation date in ascending
     *     order.
     */
    public Builder fileIds(List<String> fileIds) {
      this.fileIds = Optional.of(fileIds);
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

    public CreateAssistantRequest build() {
      return new CreateAssistantRequest(
          model,
          name,
          description,
          instructions,
          tools.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(tools)),
          fileIds,
          metadata);
    }
  }
}
