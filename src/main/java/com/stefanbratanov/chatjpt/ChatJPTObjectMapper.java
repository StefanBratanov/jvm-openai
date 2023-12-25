package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class ChatJPTObjectMapper {

  private static final ObjectMapper OBJECT_MAPPER;

  static {
    ObjectMapper objectMapper = JsonMapper.builder().addModule(new Jdk8Module()).build();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    OBJECT_MAPPER = objectMapper;
  }

  static ObjectMapper getInstance() {
    return OBJECT_MAPPER;
  }
}
