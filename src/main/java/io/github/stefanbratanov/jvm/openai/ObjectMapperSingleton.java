package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class ObjectMapperSingleton {

  private static ObjectMapper INSTANCE;

  private ObjectMapperSingleton() {}

  static ObjectMapper getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ObjectMapper();
      INSTANCE.registerModule(new Jdk8Module());
      INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      INSTANCE.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
      INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
    return INSTANCE;
  }
}
