package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

class ErrorDeserializer extends StdDeserializer<Error> {

  ErrorDeserializer() {
    super(Error.class);
  }

  @Override
  public Error deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.readValueAsTree();

    JsonNode error = node.get("error");

    String message = error.get("message").asText();
    String type = error.get("type").asText();

    return new Error(message, type);
  }
}
