package io.github.stefanbratanov.jvm.openai;

/** Specifies a tool the model should use. Use to force the model to call a specific function. */
public record ToolChoice(String type, Function function) implements AssistantsToolChoice {

  /**
   * @param name The name of the function to call.
   */
  public record Function(String name) {}

  public static ToolChoice functionToolChoice(Function function) {
    return new ToolChoice(Constants.FUNCTION_TOOL_TYPE, function);
  }

  public static ToolChoice codeInterpreterToolChoice() {
    return new ToolChoice(Constants.CODE_INTERPRETER_TOOL_TYPE, null);
  }

  public static ToolChoice retrievalToolChoice() {
    return new ToolChoice(Constants.RETRIEVAL_TOOL_TYPE, null);
  }
}
