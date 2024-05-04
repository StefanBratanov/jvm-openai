package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getStringResource;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
