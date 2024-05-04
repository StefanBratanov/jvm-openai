package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Function that the model may generate JSON inputs for. */
public record Function(
    String name, Optional<String> description, Optional<Map<String, Object>> parameters) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private Optional<String> description = Optional.empty();
    private Optional<Map<String, Object>> parameters = Optional.empty();

    /**
     * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or contain
     *     underscores and dashes, with a maximum length of 64.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * @param description A description of what the function does, used by the model to choose when
     *     and how to call the function.
     */
    public Builder description(String description) {
      this.description = Optional.of(description);
      return this;
    }

    /**
     * @param parameters The parameters the functions accepts, described as a JSON Schema object.
     *     The JSON schema should be defined as {@link Map} where a value could be a raw escaped
     *     JSON {@link String} and it will be serialized without escaping.
     */
    public Builder parameters(Map<String, Object> parameters) {
      this.parameters = Optional.of(parameters);
      return this;
    }

    public Function build() {
      return new Function(name, description, parameters.map(this::parametersWithoutJsonEscaping));
    }

    private Map<String, Object> parametersWithoutJsonEscaping(Map<String, Object> parameters) {
      return parameters.entrySet().stream()
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
}
