package io.github.stefanbratanov.jvm.openai;

/** Could be either {@link StringToolChoice} or {@link ToolChoice} */
public sealed interface AssistantsToolChoice
    permits AssistantsToolChoice.StringToolChoice, ToolChoice {

  /**
   * @param choice `none` means the model will not call a function and instead generates a message.
   *     `auto` means the model can pick between generating a message or calling a function.
   */
  record StringToolChoice(String choice) implements AssistantsToolChoice {}

  static AssistantsToolChoice none() {
    return new StringToolChoice("none");
  }

  static AssistantsToolChoice auto() {
    return new StringToolChoice("auto");
  }

  static AssistantsToolChoice namedToolChoice(ToolChoice toolChoice) {
    return toolChoice;
  }
}
