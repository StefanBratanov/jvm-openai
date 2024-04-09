package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestUtil.getStringResource;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stefanbratanov.jvm.openai.FineTuningJobCheckpoint.Metrics;
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

  @Test
  void deserializesFineTuningJobCheckpoint() throws JsonProcessingException {
    FineTuningJobCheckpoint fineTuningJobCheckpoint =
        objectMapper.readValue(
            getStringResource("/fine-tuning-job-checkpoint.json"), FineTuningJobCheckpoint.class);

    assertThat(fineTuningJobCheckpoint).isNotNull();
    assertThat(fineTuningJobCheckpoint.id()).isEqualTo("ftckpt_qtZ5Gyk4BLq1SfLFWp3RtO3P");
    assertThat(fineTuningJobCheckpoint.fineTunedModelCheckpoint())
        .isEqualTo("ft:gpt-3.5-turbo-0125:my-org:custom_suffix:9ABel2dg:ckpt-step-88");
    assertThat(fineTuningJobCheckpoint.fineTuningJobId())
        .isEqualTo("ftjob-fpbNQ3H1GrMehXRf8cO97xTN");
    Metrics metrics = fineTuningJobCheckpoint.metrics();

    assertThat(metrics.step()).isEqualTo(88.0);
    assertThat(metrics.trainMeanTokenAccuracy()).isEqualTo(0.924);
    assertThat(metrics.fullValidLoss()).isEqualTo(0.567);

    assertThat(fineTuningJobCheckpoint.stepNumber()).isEqualTo(88);
  }
}
