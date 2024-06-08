package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = Tool.CodeInterpreterTool.class,
      name = Constants.CODE_INTERPRETER_TOOL_TYPE),
  @JsonSubTypes.Type(value = Tool.FileSearchTool.class, name = Constants.FILE_SEARCH_TOOL_TYPE),
  @JsonSubTypes.Type(value = Tool.FunctionTool.class, name = Constants.FUNCTION_TOOL_TYPE)
})
public sealed interface Tool
    permits Tool.CodeInterpreterTool, Tool.FileSearchTool, Tool.FunctionTool {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record CodeInterpreterTool() implements Tool {

    @Override
    public String type() {
      return Constants.CODE_INTERPRETER_TOOL_TYPE;
    }
  }

  record FileSearchTool(Optional<FileSearch> fileSearch) implements Tool {

    public record FileSearch(Optional<Integer> maxNumResults) {}

    @Override
    public String type() {
      return Constants.FILE_SEARCH_TOOL_TYPE;
    }
  }

  record FunctionTool(Function function) implements Tool {

    @Override
    public String type() {
      return Constants.FUNCTION_TOOL_TYPE;
    }
  }

  static CodeInterpreterTool codeInterpreterTool() {
    return new CodeInterpreterTool();
  }

  static FileSearchTool fileSearchTool() {
    return new FileSearchTool(Optional.empty());
  }

  /**
   * @param maxNumResults The maximum number of results the file search tool should output.
   */
  static FileSearchTool fileSearchTool(int maxNumResults) {
    return new FileSearchTool(
        Optional.of(new FileSearchTool.FileSearch(Optional.of(maxNumResults))));
  }

  static FunctionTool functionTool(Function function) {
    return new FunctionTool(function);
  }
}
