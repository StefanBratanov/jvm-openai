package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

class ModelDeserializer extends StdDeserializer<Model> {

  ModelDeserializer() {
    super(Model.class);
  }

  @Override
  public Model deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.readValueAsTree();

    String id = node.get("id").asText();
    long created = node.get("created").asLong();
    String ownedBy = node.get("owned_by").asText();

    return new Model(id, created, ownedBy);
  }
}
