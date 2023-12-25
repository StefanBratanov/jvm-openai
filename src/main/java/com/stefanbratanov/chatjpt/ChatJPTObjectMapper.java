package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class ChatJPTObjectMapper {

  private static final ObjectMapper OBJECT_MAPPER;

  static {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    OBJECT_MAPPER = objectMapper;
  }

  static ObjectMapper getInstance() {
    return OBJECT_MAPPER;
  }
}
