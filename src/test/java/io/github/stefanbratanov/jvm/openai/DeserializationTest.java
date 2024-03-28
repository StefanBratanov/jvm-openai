package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getStringResource;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class DeserializationTest {

  private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

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
}
