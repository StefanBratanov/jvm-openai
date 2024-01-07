package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.stefanbratanov.chatjpt.Tool.FunctionTool.Function;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = Tool.CodeInterpreterTool.class,
      name = Constants.CODE_INTERPRETER_TOOL_TYPE),
  @JsonSubTypes.Type(value = Tool.RetrievalTool.class, name = Constants.RETRIEVAL_TOOL_TYPE),
  @JsonSubTypes.Type(value = Tool.FunctionTool.class, name = Constants.FUNCTION_TOOL_TYPE)
})
public sealed interface Tool
    permits Tool.CodeInterpreterTool, Tool.RetrievalTool, Tool.FunctionTool {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record CodeInterpreterTool() implements Tool {

    @Override
    public String type() {
      return Constants.CODE_INTERPRETER_TOOL_TYPE;
    }
  }

  record RetrievalTool() implements Tool {

    @Override
    public String type() {
      return Constants.RETRIEVAL_TOOL_TYPE;
    }
  }

  record FunctionTool(Function function) implements Tool {

    @Override
    public String type() {
      return Constants.FUNCTION_TOOL_TYPE;
    }

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
         * @param parameters The parameters the functions accepts, described as a JSON Schema
         *     object. The JSON schema should be defined as a {@link Map}
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
  }

  static CodeInterpreterTool codeInterpreterTool() {
    return new CodeInterpreterTool();
  }

  static RetrievalTool retrievalTool() {
    return new RetrievalTool();
  }

  static FunctionTool functionTool(Function function) {
    return new FunctionTool(function);
  }
}
