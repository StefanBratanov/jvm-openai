package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

class AssistantsToolChoiceSerializer extends StdSerializer<AssistantsToolChoice> {

  AssistantsToolChoiceSerializer() {
    super(AssistantsToolChoice.class);
  }

  @Override
  public void serialize(AssistantsToolChoice value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    if (value instanceof AssistantsToolChoice.StringToolChoice toolChoice) {
      gen.writeString(toolChoice.choice());
    } else if (value instanceof ToolChoice toolChoice) {
      gen.writeStartObject();
      gen.writeStringField("type", toolChoice.type());
      if (toolChoice.function() != null) {
        gen.writeObjectField("function", toolChoice.function());
      }
      gen.writeEndObject();
    }
  }
}
