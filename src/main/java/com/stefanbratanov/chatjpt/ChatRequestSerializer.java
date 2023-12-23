package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

class ChatRequestSerializer extends StdSerializer<ChatRequest> {

  ChatRequestSerializer() {
    super(ChatRequest.class);
  }

  @Override
  public void serialize(ChatRequest value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField("model", value.model());
    gen.writeArrayFieldStart("messages");
    for (Message message : value.messages()) {
      gen.writeObject(message);
    }
    gen.writeEndArray();
    gen.writeEndObject();
  }
}
