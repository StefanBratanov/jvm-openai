package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class ObjectMapperSingleton {

  private static ObjectMapper instance;

  private ObjectMapperSingleton() {}

  static synchronized ObjectMapper getInstance() {
    if (instance == null) {
      instance =
          JsonMapper.builder()
              .addModule(new Jdk8Module())
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              .serializationInclusion(JsonInclude.Include.NON_ABSENT)
              .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
              .build();
    }
    return instance;
  }
}
