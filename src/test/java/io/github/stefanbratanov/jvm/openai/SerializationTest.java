package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getStringResource;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

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
}
