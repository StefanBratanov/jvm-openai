package io.github.stefanbratanov.jvm.openai;

/** Specifies a tool the model should use. Use to force the model to call a specific function. */
public record ToolChoice(String type, Function function) {

  /**
   * @param name The name of the function to call.
   */
  public record Function(String name) {}
}
