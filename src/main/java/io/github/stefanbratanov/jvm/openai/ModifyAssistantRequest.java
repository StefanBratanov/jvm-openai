package io.github.stefanbratanov.jvm.openai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ModifyAssistantRequest(
    Optional<String> model,
    Optional<String> name,
    Optional<String> description,
    Optional<String> instructions,
    Optional<List<Tool>> tools,
    Optional<ToolResources> toolResources,
    Optional<Map<String, String>> metadata,
    Optional<Double> temperature,
    Optional<Double> topP,
    Optional<AssistantsResponseFormat> responseFormat) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Optional<String> model = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<String> instructions = Optional.empty();

    private final List<Tool> tools = new LinkedList<>();

    private Optional<ToolResources> toolResources = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();
    private Optional<Double> temperature = Optional.empty();
    private Optional<Double> topP = Optional.empty();
    private Optional<AssistantsResponseFormat> responseFormat = Optional.empty();

    /**
     * @param model ID of the model to use.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    /**
     * @param model {@link OpenAIModel} to use.
     */
    public Builder model(OpenAIModel model) {
      this.model = Optional.of(model.getId());
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
     *     256,000 characters.
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
     * @param toolResources A set of resources that are used by the assistant's tools. The resources
     *     are specific to the type of tool. For example, the code_interpreter tool requires a list
     *     of file IDs, while the file_search tool requires a list of vector store IDs.
     */
    public Builder toolResources(ToolResources toolResources) {
      this.toolResources = Optional.of(toolResources);
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
    public Builder temperature(double temperature) {
      this.temperature = Optional.of(temperature);
      return this;
    }

    /**
     * @param topP An alternative to sampling with temperature, called nucleus sampling, where the
     *     model considers the results of the tokens with top_p probability mass. So 0.1 means only
     *     the tokens comprising the top 10% probability mass are considered.
     */
    public Builder topP(double topP) {
      this.topP = Optional.of(topP);
      return this;
    }

    /**
     * @param responseFormat An object specifying the format that the model must output.
     */
    public Builder responseFormat(AssistantsResponseFormat responseFormat) {
      this.responseFormat = Optional.of(responseFormat);
      return this;
    }

    public ModifyAssistantRequest build() {
      return new ModifyAssistantRequest(
          model,
          name,
          description,
          instructions,
          tools.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(tools)),
          toolResources,
          metadata,
          temperature,
          topP,
          responseFormat);
    }
  }
}
