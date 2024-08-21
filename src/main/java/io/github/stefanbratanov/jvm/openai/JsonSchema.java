package io.github.stefanbratanov.jvm.openai;

import java.util.Map;
import java.util.Optional;

public record JsonSchema(
    String name,
    Optional<String> description,
    Optional<Map<String, Object>> schema,
    Optional<Boolean> strict) {

  public JsonSchema {
    schema = schema.map(Utils::mapWithoutJsonEscaping);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private Optional<String> description = Optional.empty();
    private Optional<Map<String, Object>> schema = Optional.empty();
    private Optional<Boolean> strict = Optional.empty();

    /**
     * @param name The name of the response format.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * @param description A description of what the response format is for, used by the model to
     *     determine how to respond in the format.
     */
    public Builder description(String description) {
      this.description = Optional.of(description);
      return this;
    }

    /**
     * @param schema The schema for the response format, described as a JSON Schema object. The JSON
     *     schema should be defined as {@link Map} where a value could be a raw escaped JSON {@link
     *     String} and it will be serialized without escaping.
     */
    public Builder schema(Map<String, Object> schema) {
      this.schema = Optional.of(schema);
      return this;
    }

    /**
     * @param strict Whether to enable strict schema adherence when generating the output. If set to
     *     true, the model will always follow the exact schema defined in the schema field. Only a
     *     subset of JSON Schema is supported when strict is true.
     */
    public Builder strict(boolean strict) {
      this.strict = Optional.of(strict);
      return this;
    }

    public JsonSchema build() {
      return new JsonSchema(name, description, schema, strict);
    }
  }
}
