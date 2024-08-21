package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Optional;

class AssistantsResponseFormatSerializer extends StdSerializer<AssistantsResponseFormat> {

  AssistantsResponseFormatSerializer() {
    super(AssistantsResponseFormat.class);
  }

  @Override
  public void serialize(
      AssistantsResponseFormat value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    if (value instanceof AssistantsResponseFormat.StringResponseFormat responseFormat) {
      gen.writeString(responseFormat.format());
    } else if (value instanceof ResponseFormat responseFormat) {
      gen.writeStartObject();
      gen.writeStringField("type", responseFormat.type());
      Optional<JsonSchema> jsonSchema = responseFormat.jsonSchema();
      if (jsonSchema.isPresent()) {
        gen.writeObjectField("json_schema", jsonSchema.get());
      }
      gen.writeEndObject();
    }
  }
}
