package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getStringResource;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stefanbratanov.jvm.openai.DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent.Text.Annotation;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent.Text.Annotation.FileCitationAnnotation.FileCitation;
import io.github.stefanbratanov.jvm.openai.ThreadMessageDelta.Delta;
import io.github.stefanbratanov.jvm.openai.ThreadMessageDelta.Delta.Content.TextContent.Text;
import io.github.stefanbratanov.jvm.openai.ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation.FilePathAnnotation.FilePath;
import io.github.stefanbratanov.jvm.openai.ThreadRunStep.StepDetails;
import io.github.stefanbratanov.jvm.openai.ThreadRunStepDelta.StepDetails.MessageCreationStepDetails;
import io.github.stefanbratanov.jvm.openai.ThreadRunStepDelta.StepDetails.MessageCreationStepDetails.MessageCreation;
import io.github.stefanbratanov.jvm.openai.ToolCall.CodeInterpreterToolCall.CodeInterpreter.Output;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class SerializationTest {

  private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
  private final TestDataUtil testDataUtil = new TestDataUtil();

  @Test
  void deserializesChatCompletion() throws JsonProcessingException {
    ChatCompletion result =
        objectMapper.readValue(getStringResource("/chat-completion.json"), ChatCompletion.class);

    assertThat(result.choices())
        .hasSize(1)
        .first()
        .satisfies(
            choice -> {
              List<ToolCall> toolCalls = choice.message().toolCalls();
              assertThat(toolCalls).hasSize(1);
              ToolCall toolCall = toolCalls.get(0);
              assertThat(toolCall).isInstanceOf(ToolCall.FunctionToolCall.class);
              assertThat(((ToolCall.FunctionToolCall) toolCall).function().arguments())
                  .isEqualTo("{\"person_name\":\"ran\"}");
            });
  }

  @Test
  void deserializesChatCompletionChunk() throws JsonProcessingException {
    ChatCompletionChunk result =
        objectMapper.readValue(
            getStringResource("/chat-completion-chunk.json"), ChatCompletionChunk.class);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo("chatcmpl-xyz");
    assertThat(result.choices())
        .hasSize(1)
        .first()
        .satisfies(
            choice -> {
              assertThat(choice.index()).isZero();
              assertThat(choice.delta().role()).isEqualTo("assistant");
              assertThat(choice.delta().content()).isEmpty();
              assertThat(choice.finishReason()).isNull();
            });
  }

  @RepeatedTest(50)
  void serializesAndDeserializesThreadMessageDelta() throws JsonProcessingException {
    ThreadMessageDelta threadMessageDelta = testDataUtil.randomThreadMessageDelta();

    String serialized = objectMapper.writeValueAsString(threadMessageDelta);

    assertThat(objectMapper.readValue(serialized, ThreadMessageDelta.class))
        .isEqualTo(threadMessageDelta);
  }

  @RepeatedTest(50)
  void serializesAndDeserializesThreadRunStepDelta() throws JsonProcessingException {
    ThreadRunStepDelta threadRunStepDelta = testDataUtil.randomThreadRunStepDelta();

    String serialized = objectMapper.writeValueAsString(threadRunStepDelta);

    assertThat(objectMapper.readValue(serialized, ThreadRunStepDelta.class))
        .isEqualTo(threadRunStepDelta);
  }

  @Test
  void deserializesAssistantsToolChoice() throws JsonProcessingException {
    List<AssistantsToolChoice> choices =
        objectMapper.readValue(
            getStringResource("/assistants-tool-choices.json"), new TypeReference<>() {});

    assertThat(choices).hasSize(5);

    assertThat(choices.get(0))
        .isInstanceOfSatisfying(
            AssistantsToolChoice.StringToolChoice.class,
            choice -> assertThat(choice.choice()).isEqualTo("none"));
    assertThat(choices.get(1))
        .isInstanceOfSatisfying(
            AssistantsToolChoice.StringToolChoice.class,
            choice -> assertThat(choice.choice()).isEqualTo("auto"));
    assertThat(choices.get(2))
        .isInstanceOfSatisfying(
            ToolChoice.class,
            choice -> {
              assertThat(choice.type()).isEqualTo("code_interpreter");
              assertThat(choice.function()).isNull();
            });
    assertThat(choices.get(3))
        .isInstanceOfSatisfying(
            ToolChoice.class,
            choice -> {
              assertThat(choice.type()).isEqualTo("file_search");
              assertThat(choice.function()).isNull();
            });
    assertThat(choices.get(4))
        .isInstanceOfSatisfying(
            ToolChoice.class,
            choice -> {
              assertThat(choice.type()).isEqualTo("function");
              assertThat(choice.function()).isEqualTo(new ToolChoice.Function("foo"));
            });
  }

  @Test
  void deserializesAssistantsResponseFormat() throws JsonProcessingException {
    List<AssistantsResponseFormat> choices =
        objectMapper.readValue(
            getStringResource("/assistants-response-formats.json"), new TypeReference<>() {});

    assertThat(choices).hasSize(4);

    assertThat(choices.get(0))
        .isInstanceOfSatisfying(
            AssistantsResponseFormat.StringResponseFormat.class,
            format -> assertThat(format.format()).isEqualTo("none"));
    assertThat(choices.get(1))
        .isInstanceOfSatisfying(
            AssistantsResponseFormat.StringResponseFormat.class,
            format -> assertThat(format.format()).isEqualTo("auto"));
    assertThat(choices.get(2))
        .isInstanceOfSatisfying(
            ResponseFormat.class, format -> assertThat(format.type()).isEqualTo("text"));
    assertThat(choices.get(3))
        .isInstanceOfSatisfying(
            ResponseFormat.class, format -> assertThat(format.type()).isEqualTo("json_object"));
  }

  @Test
  void serializesFunction() throws JsonProcessingException, JSONException {
    Function function =
        Function.newBuilder()
            .name("getFriends")
            .description("Returns the friends of the person")
            .parameters(
                Map.of(
                    "type",
                    "object",
                    "properties",
                    "{\"person_name\":{\"type\":\"string\", \"description\":\"the persons name, in lower case\"}}",
                    "required",
                    "[\"person_name\"]"))
            .build();

    JSONAssert.assertEquals(
        getStringResource("/function.json"),
        objectMapper.writeValueAsString(function),
        JSONCompareMode.STRICT);
  }

  @Test
  void doesNotSerializeTypeTwiceForJsonSubTypesAnnotatedClasses() throws JsonProcessingException {
    Tool.FileSearchTool fileSearchTool = Tool.fileSearchTool();

    assertThat(objectMapper.writeValueAsString(fileSearchTool))
        .isEqualTo("{\"type\":\"file_search\"}");

    ToolCall.FileSearchToolCall fileSearchToolCall = ToolCall.fileSearchToolCall("foobar");

    assertThat(objectMapper.writeValueAsString(fileSearchToolCall))
        .isEqualTo("{\"id\":\"foobar\",\"file_search\":{},\"type\":\"file_search\"}");

    DeltaToolCall.FileSearchToolCall deltaFileSearchToolCall =
        DeltaToolCall.fileSearchToolCall(0, "foobar");

    assertThat(objectMapper.writeValueAsString(deltaFileSearchToolCall))
        .isEqualTo("{\"index\":0,\"id\":\"foobar\",\"file_search\":{},\"type\":\"file_search\"}");

    TextContent textContent = new TextContent(new TextContent.Text("foobar", List.of()));

    assertThat(objectMapper.writeValueAsString(textContent))
        .isEqualTo("{\"text\":{\"value\":\"foobar\",\"annotations\":[]},\"type\":\"text\"}");

    MessageCreationStepDetails messageCreationStepDetails =
        new MessageCreationStepDetails(new MessageCreation("foobar"));

    assertThat(objectMapper.writeValueAsString(messageCreationStepDetails))
        .isEqualTo(
            "{\"message_creation\":{\"message_id\":\"foobar\"},\"type\":\"message_creation\"}");

    Delta.Content.TextContent deltaTextContent =
        new Delta.Content.TextContent(0, new Text("foobar", List.of()));

    assertThat(objectMapper.writeValueAsString(deltaTextContent))
        .isEqualTo(
            "{\"index\":0,\"text\":{\"value\":\"foobar\",\"annotations\":[]},\"type\":\"text\"}");

    StepDetails.MessageCreationStepDetails runMessageCreationStepDetails =
        new StepDetails.MessageCreationStepDetails(
            new StepDetails.MessageCreationStepDetails.MessageCreation("foobar"));

    assertThat(objectMapper.writeValueAsString(runMessageCreationStepDetails))
        .isEqualTo(
            "{\"message_creation\":{\"message_id\":\"foobar\"},\"type\":\"message_creation\"}");

    Output.LogOutput logOutput = Output.logOutput("foobar");

    assertThat(objectMapper.writeValueAsString(logOutput))
        .isEqualTo("{\"logs\":\"foobar\",\"type\":\"logs\"}");

    DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.LogOutput deltaLogOutput =
        CodeInterpreter.Output.logOutput(0, "foobar");

    assertThat(objectMapper.writeValueAsString(deltaLogOutput))
        .isEqualTo("{\"index\":0,\"logs\":\"foobar\",\"type\":\"logs\"}");

    Annotation annotation =
        new Annotation.FileCitationAnnotation("foobar", new FileCitation("foobar"), 0, 0);

    assertThat(objectMapper.writeValueAsString(annotation))
        .isEqualTo(
            "{\"text\":\"foobar\",\"file_citation\":{\"file_id\":\"foobar\"},\"start_index\":0,\"end_index\":0,\"type\":\"file_citation\"}");

    Delta.Content.TextContent.Text.Annotation.FilePathAnnotation deltaAnnotation =
        new Text.Annotation.FilePathAnnotation(0, "foobar", new FilePath("foobar"), 0, 0);

    assertThat(objectMapper.writeValueAsString(deltaAnnotation))
        .isEqualTo(
            "{\"index\":0,\"text\":\"foobar\",\"file_path\":{\"file_id\":\"foobar\"},\"start_index\":0,\"end_index\":0,\"type\":\"file_path\"}");
  }
}
