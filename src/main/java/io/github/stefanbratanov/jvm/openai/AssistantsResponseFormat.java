package io.github.stefanbratanov.jvm.openai;

/** Could be either {@link StringResponseFormat} or {@link ResponseFormat} */
public sealed interface AssistantsResponseFormat
    permits AssistantsResponseFormat.StringResponseFormat, ResponseFormat {

  /**
   * @param format `auto` is the default value
   */
  record StringResponseFormat(String format) implements AssistantsResponseFormat {}

  static AssistantsResponseFormat auto() {
    return new StringResponseFormat("auto");
  }

  static AssistantsResponseFormat responseFormat(ResponseFormat responseFormat) {
    return responseFormat;
  }
}
