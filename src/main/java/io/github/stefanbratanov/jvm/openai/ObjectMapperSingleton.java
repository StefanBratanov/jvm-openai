package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class ObjectMapperSingleton {

  private static ObjectMapper instance;

  private ObjectMapperSingleton() {}

  static ObjectMapper getInstance() {
    if (instance == null) {
      instance = new ObjectMapper();
      instance.registerModule(new Jdk8Module());
      instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      instance.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
      instance.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
    return instance;
  }
}
