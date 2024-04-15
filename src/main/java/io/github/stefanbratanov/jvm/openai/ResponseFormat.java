package io.github.stefanbratanov.jvm.openai;

/** An object specifying the format that the model must output. */
public record ResponseFormat(String type) implements AssistantsResponseFormat {
  public static ResponseFormat text() {
    return new ResponseFormat("text");
  }

  public static ResponseFormat json() {
    return new ResponseFormat("json_object");
  }
}
