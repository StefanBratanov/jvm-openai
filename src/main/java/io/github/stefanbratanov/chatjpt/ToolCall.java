package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.stefanbratanov.chatjpt.ToolCall.CodeInterpreterToolCall.CodeInterpreter;
import io.github.stefanbratanov.chatjpt.ToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.ImageOutput.Image;
import io.github.stefanbratanov.chatjpt.ToolCall.FunctionToolCall.Function;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = ToolCall.CodeInterpreterToolCall.class,
      name = Constants.CODE_INTERPRETER_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = ToolCall.RetrievalToolCall.class,
      name = Constants.RETRIEVAL_TOOL_CALL_TYPE),
  @JsonSubTypes.Type(
      value = ToolCall.FunctionToolCall.class,
      name = Constants.FUNCTION_TOOL_CALL_TYPE)
})
public sealed interface ToolCall
    permits ToolCall.CodeInterpreterToolCall,
        ToolCall.RetrievalToolCall,
        ToolCall.FunctionToolCall {

  String id();

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

      @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
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

  record RetrievalToolCall(String id, Map<String, Object> retrieval) implements ToolCall {
    @Override
    public String type() {
      return Constants.RETRIEVAL_TOOL_CALL_TYPE;
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

  static RetrievalToolCall retrievalToolCall(String id) {
    return new RetrievalToolCall(id, Collections.emptyMap());
  }

  static FunctionToolCall functionToolCall(String id, Function function) {
    return new FunctionToolCall(id, function);
  }
}
