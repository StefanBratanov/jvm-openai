package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

class ChatJPTObjectMapper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    SimpleModule module = new SimpleModule();
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    module
        .addSerializer(ChatRequest.class, new ChatRequestSerializer())
        .addDeserializer(ChatResponse.class, new ChatResponseDeserializer())
        .addDeserializer(Model.class, new ModelDeserializer())
        .addDeserializer(Error.class, new ErrorDeserializer());
    OBJECT_MAPPER.registerModule(module);
  }

  static ObjectMapper getInstance() {
    return OBJECT_MAPPER;
  }
}
