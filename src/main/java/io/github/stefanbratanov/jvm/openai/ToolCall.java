package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.stefanbratanov.jvm.openai.ToolCall.CodeInterpreterToolCall.CodeInterpreter;
import io.github.stefanbratanov.jvm.openai.ToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.ImageOutput.Image;
import io.github.stefanbratanov.jvm.openai.ToolCall.FileSearchToolCall.FileSearch;
import io.github.stefanbratanov.jvm.openai.ToolCall.FunctionToolCall.Function;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = ToolCall.CodeInterpreterToolCall.class,
      name = Constants.CODE_INTERPRETER_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = ToolCall.FileSearchToolCall.class,
      name = Constants.FILE_SEARCH_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = ToolCall.FunctionToolCall.class,
      name = Constants.FUNCTION_TOOL_CALL_TYPE)
})
public sealed interface ToolCall
    permits ToolCall.CodeInterpreterToolCall,
        ToolCall.FileSearchToolCall,
        ToolCall.FunctionToolCall {

  /** The ID of the tool call. */
  String id();

  /** The type of tool call */
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record CodeInterpreterToolCall(String id, CodeInterpreter codeInterpreter) implements ToolCall {
    @Override
    public String type() {
      return Constants.CODE_INTERPRETER_TOOL_CALL_TYPE;
    }

    /**
     * @param input The input to the Code Interpreter tool call.
     * @param outputs The outputs from the Code Interpreter tool call. Code Interpreter can output
     *     one or more items, including text (logs) or images (image). Each of these are represented
     *     by a different object type.
     */
    public record CodeInterpreter(String input, List<Output> outputs) {

      @JsonTypeInfo(
          use = JsonTypeInfo.Id.NAME,
          property = "type",
          include = JsonTypeInfo.As.EXISTING_PROPERTY)
      @JsonSubTypes({
        @JsonSubTypes.Type(
            value = Output.LogOutput.class,
            name = Constants.CODE_INTERPRETER_LOG_OUTPUT_TYPE),
        @JsonSubTypes.Type(
            value = Output.ImageOutput.class,
            name = Constants.CODE_INTERPRETER_IMAGE_OUTPUT_TYPE),
      })
      public sealed interface Output permits Output.LogOutput, Output.ImageOutput {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String type();

        record LogOutput(String logs) implements Output {

          @Override
          public String type() {
            return Constants.CODE_INTERPRETER_LOG_OUTPUT_TYPE;
          }
        }

        record ImageOutput(Image image) implements Output {

          @Override
          public String type() {
            return Constants.CODE_INTERPRETER_IMAGE_OUTPUT_TYPE;
          }

          public record Image(String fileId) {}
        }

        static LogOutput logOutput(String logs) {
          return new LogOutput(logs);
        }

        static ImageOutput imageOutput(Image image) {
          return new ImageOutput(image);
        }
      }
    }
  }

  record FileSearchToolCall(String id, FileSearch fileSearch) implements ToolCall {
    @Override
    public String type() {
      return Constants.FILE_SEARCH_TOOL_CALL_TYPE;
    }

    public record FileSearch(RankingOptions rankingOptions, List<Result> results) {

      public record RankingOptions(String ranker, double scoreThreshold) {}

      public record Result(String fileId, String fileName, double score, List<Content> content) {

        public record Content(String type, String text) {}
      }
    }
  }

  record FunctionToolCall(String id, Function function) implements ToolCall {
    @Override
    public String type() {
      return Constants.FUNCTION_TOOL_CALL_TYPE;
    }

    /**
     * @param name The name of the function.
     * @param arguments The arguments passed to the function.
     * @param output The output of the function. This will be null if the outputs have not been
     *     submitted yet.
     */
    public record Function(String name, String arguments, String output) {}
  }

  static CodeInterpreterToolCall codeInterpreterToolCall(
      String id, CodeInterpreter codeInterpreter) {
    return new CodeInterpreterToolCall(id, codeInterpreter);
  }

  static FileSearchToolCall fileSearchToolCall(String id, FileSearch fileSearch) {
    return new FileSearchToolCall(id, fileSearch);
  }

  static FunctionToolCall functionToolCall(String id, Function function) {
    return new FunctionToolCall(id, function);
  }
}
