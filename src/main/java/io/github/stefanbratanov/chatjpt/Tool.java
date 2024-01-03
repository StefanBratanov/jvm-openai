package io.github.stefanbratanov.chatjpt;

import java.util.Map;
import java.util.Optional;

public record Tool(String type, Function function) {

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
       * @param description A description of what the function does, used by the model to choose
       *     when and how to call the function.
       */
      public Builder description(String description) {
        this.description = Optional.of(description);
        return this;
      }

      /**
       * @param parameters The parameters the functions accepts, described as a JSON Schema object.
       *     The JSON schema should be defined as a {@link Map}
       */
      public Builder parameters(Map<String, Object> parameters) {
        this.parameters = Optional.of(parameters);
        return this;
      }

      public Function build() {
        if (name == null) {
          throw new IllegalStateException("name must be set");
        }
        return new Function(name, description, parameters);
      }
    }
  }

  public static Tool functionTool(Function function) {
    return new Tool("function", function);
  }
}
