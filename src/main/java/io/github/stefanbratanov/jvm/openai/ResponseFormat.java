package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

/** An object specifying the format that the model must output. */
public record ResponseFormat(String type, Optional<JsonSchema> jsonSchema)
    implements AssistantsResponseFormat {
  public static ResponseFormat text() {
    return new ResponseFormat("text", Optional.empty());
  }

  public static ResponseFormat json() {
    return new ResponseFormat("json_object", Optional.empty());
  }

  public static ResponseFormat jsonSchema(JsonSchema jsonSchema) {
    return new ResponseFormat("json_schema", Optional.of(jsonSchema));
  }
}
