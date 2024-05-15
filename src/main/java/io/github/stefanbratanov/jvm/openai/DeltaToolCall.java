package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.stefanbratanov.jvm.openai.DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter;
import io.github.stefanbratanov.jvm.openai.DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.ImageOutput.Image;
import io.github.stefanbratanov.jvm.openai.DeltaToolCall.FunctionToolCall.Function;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Details of the tool call the {@link ThreadRunStepDelta} was involved in. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = DeltaToolCall.CodeInterpreterToolCall.class,
      name = Constants.CODE_INTERPRETER_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = DeltaToolCall.FileSearchToolCall.class,
      name = Constants.FILE_SEARCH_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = DeltaToolCall.FunctionToolCall.class,
      name = Constants.FUNCTION_TOOL_CALL_TYPE)
})
public sealed interface DeltaToolCall
    permits DeltaToolCall.CodeInterpreterToolCall,
        DeltaToolCall.FileSearchToolCall,
        DeltaToolCall.FunctionToolCall {

  /** The index of the tool call in the tool calls array. */
  int index();

  /** The ID of the tool call object. */
  String id();

  /** The type of tool call */
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record CodeInterpreterToolCall(int index, String id, CodeInterpreter codeInterpreter)
      implements DeltaToolCall {
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
        /** The index of the output in the outputs array. */
        int index();

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String type();

        record LogOutput(int index, String logs) implements Output {

          @Override
          public String type() {
            return Constants.CODE_INTERPRETER_LOG_OUTPUT_TYPE;
          }
        }

        record ImageOutput(int index, Image image) implements Output {

          @Override
          public String type() {
            return Constants.CODE_INTERPRETER_IMAGE_OUTPUT_TYPE;
          }

          public record Image(String fileId) {}
        }

        static LogOutput logOutput(int index, String logs) {
          return new LogOutput(index, logs);
        }

        static ImageOutput imageOutput(int index, Image image) {
          return new ImageOutput(index, image);
        }
      }
    }
  }

  record FileSearchToolCall(int index, String id, Map<String, Object> fileSearch)
      implements DeltaToolCall {
    @Override
    public String type() {
      return Constants.FILE_SEARCH_TOOL_CALL_TYPE;
    }
  }

  record FunctionToolCall(int index, String id, Function function) implements DeltaToolCall {
    @Override
    public String type() {
      return Constants.FUNCTION_TOOL_CALL_TYPE;
    }

    /**
     * @param index The index of the tool call in the tool calls array.
     * @param name The name of the function.
     * @param arguments The arguments passed to the function.
     * @param output The output of the function. This will be null if the outputs have not been
     *     submitted yet.
     */
    public record Function(int index, String name, String arguments, String output) {}
  }

  static CodeInterpreterToolCall codeInterpreterToolCall(
      int index, String id, CodeInterpreter codeInterpreter) {
    return new CodeInterpreterToolCall(index, id, codeInterpreter);
  }

  static FileSearchToolCall fileSearchToolCall(int index, String id) {
    return new FileSearchToolCall(index, id, Collections.emptyMap());
  }

  static FunctionToolCall functionToolCall(int index, String id, Function function) {
    return new FunctionToolCall(index, id, function);
  }
}
