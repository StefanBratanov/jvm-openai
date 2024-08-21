package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

class Utils {

  private Utils() {}

  static Map<String, Object> mapWithoutJsonEscaping(Map<String, Object> map) {
    return map.entrySet().stream()
        .map(
            entry -> {
              if (entry.getValue() instanceof String value) {
                try {
                  JsonNode node = ObjectMapperSingleton.getInstance().readTree(value);
                  if (node != null && !node.isNull()) {
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), node);
                  }
                } catch (IOException ex) {
                  return entry;
                }
              }
              return entry;
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
