package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.util.Optional;

class AssistantsResponseFormatDeserializer extends StdDeserializer<AssistantsResponseFormat> {

  AssistantsResponseFormatDeserializer() {
    super(AssistantsResponseFormat.class);
  }

  @Override
  public AssistantsResponseFormat deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    if (node.isTextual()) {
      return new AssistantsResponseFormat.StringResponseFormat(node.asText());
    } else if (node.isObject()) {
      String type = node.get("type").asText();
      if (node.has("json_schema")) {
        JsonSchema jsonSchema = p.getCodec().treeToValue(node.get("json_schema"), JsonSchema.class);
        return new ResponseFormat(type, Optional.of(jsonSchema));
      } else {
        return new ResponseFormat(type, Optional.empty());
      }
    }
    throw InvalidFormatException.from(
        p, "Expected String or Object", node, AssistantsResponseFormat.class);
  }
}
