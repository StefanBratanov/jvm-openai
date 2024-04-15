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
    Optional<Map<String, String>> metadata,
    Optional<Double> temperature,
    Optional<Boolean> stream,
    Optional<Integer> maxPromptTokens,
    Optional<Integer> maxCompletionTokens,
    Optional<TruncationStrategy> truncationStrategy,
    Optional<AssistantsToolChoice> toolChoice,
    Optional<AssistantsResponseFormat> responseFormat) {

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
    private Optional<Double> temperature = Optional.empty();
    private Optional<Boolean> stream = Optional.empty();
    private Optional<Integer> maxPromptTokens = Optional.empty();
    private Optional<Integer> maxCompletionTokens = Optional.empty();
    private Optional<TruncationStrategy> truncationStrategy = Optional.empty();
    private Optional<AssistantsToolChoice> toolChoice = Optional.empty();
    private Optional<AssistantsResponseFormat> responseFormat = Optional.empty();

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
     * @param model {@link OpenAIModel} to be used to execute this run. If a value is provided here,
     *     it will override the model associated with the assistant. If not, the model associated
     *     with the assistant will be used.
     */
    public Builder model(OpenAIModel model) {
      this.model = Optional.of(model.getId());
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

    /**
     * @param temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8
     *     will make the output more random, while lower values like 0.2 will make it more focused
     *     and deterministic.
     */
    public Builder temperature(Double temperature) {
      this.temperature = Optional.of(temperature);
      return this;
    }

    /**
     * @param stream If true, returns a stream of events that happen during the Run as server-sent
     *     events
     */
    public Builder stream(Boolean stream) {
      this.stream = Optional.of(stream);
      return this;
    }

    /**
     * @param maxPromptTokens The maximum number of prompt tokens that may be used over the course
     *     of the run. The run will make a best effort to use only the number of prompt tokens
     *     specified, across multiple turns of the run. If the run exceeds the number of prompt
     *     tokens specified, the run will end with status `complete`. See `incomplete_details` for
     *     more info.
     */
    public Builder maxPromptTokens(int maxPromptTokens) {
      this.maxPromptTokens = Optional.of(maxPromptTokens);
      return this;
    }

    /**
     * @param maxCompletionTokens The maximum number of completion tokens that may be used over the
     *     course of the run. The run will make a best effort to use only the number of completion
     *     tokens specified, across multiple turns of the run. If the run exceeds the number of
     *     completion tokens specified, the run will end with status `complete`. See
     *     `incomplete_details` for more info.
     */
    public Builder maxCompletionTokens(int maxCompletionTokens) {
      this.maxCompletionTokens = Optional.of(maxCompletionTokens);
      return this;
    }

    /**
     * @param truncationStrategy The truncation strategy to use for the thread.
     */
    public Builder truncationStrategy(TruncationStrategy truncationStrategy) {
      this.truncationStrategy = Optional.of(truncationStrategy);
      return this;
    }

    /**
     * @param toolChoice Controls which (if any) tool is called by the model.
     */
    public Builder toolChoice(AssistantsToolChoice toolChoice) {
      this.toolChoice = Optional.of(toolChoice);
      return this;
    }

    /**
     * <b>Important:</b> when using JSON mode, you must also instruct the model to produce JSON
     * yourself via a system or user message.
     *
     * @param responseFormat An object specifying the format that the model must output.
     */
    public Builder responseFormat(AssistantsResponseFormat responseFormat) {
      this.responseFormat = Optional.of(responseFormat);
      return this;
    }

    public CreateThreadAndRunRequest build() {
      return new CreateThreadAndRunRequest(
          assistantId,
          thread,
          model,
          instructions,
          tools,
          metadata,
          temperature,
          stream,
          maxPromptTokens,
          maxCompletionTokens,
          truncationStrategy,
          toolChoice,
          responseFormat);
    }
  }
}
