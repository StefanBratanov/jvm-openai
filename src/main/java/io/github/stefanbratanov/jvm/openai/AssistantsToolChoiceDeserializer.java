package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;

class AssistantsToolChoiceDeserializer extends StdDeserializer<AssistantsToolChoice> {

  AssistantsToolChoiceDeserializer() {
    super(AssistantsToolChoice.class);
  }

  @Override
  public AssistantsToolChoice deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    if (node.isTextual()) {
      return new AssistantsToolChoice.StringToolChoice(node.asText());
    } else if (node.isObject()) {
      String type = node.get("type").asText();
      ToolChoice.Function function = null;
      if (node.has("function")) {
        JsonNode functionNode = node.get("function");
        function = new ToolChoice.Function(functionNode.get("name").asText());
      }
      return new ToolChoice(type, function);
    }
    throw InvalidFormatException.from(
        p, "Expected String or Object", node, AssistantsToolChoice.class);
  }
}
