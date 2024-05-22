package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record SpeechRequest(
    String model,
    String input,
    String voice,
    Optional<String> responseFormat,
    Optional<Double> speed) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = OpenAIModel.TTS_1.getId();
    private static final String DEFAULT_VOICE = Voice.ALLOY.getId();

    private String model = DEFAULT_MODEL;
    private String input;
    private String voice = DEFAULT_VOICE;
    private Optional<String> responseFormat;
    private Optional<Double> speed;

    /**
     * @param model One of the available <a href="https://platform.openai.com/docs/models/tts">TTS
     *     models</a>
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param model {@link OpenAIModel} to use. {@link OpenAIModel#TTS_1} and {@link
     *     OpenAIModel#TTS_1_HD} are available.
     */
    public Builder model(OpenAIModel model) {
      this.model = model.getId();
      return this;
    }

    /**
     * @param input The text to generate audio for
     */
    public Builder input(String input) {
      this.input = input;
      return this;
    }

    /**
     * @param voice The voice to use when generating the audio. Previews of the voices are available
     *     in the <a
     *     href="https://platform.openai.com/docs/guides/text-to-speech/voice-options">Text to
     *     speech guide</a>.
     */
    public Builder voice(String voice) {
      this.voice = voice;
      return this;
    }

    /**
     * @param voice The voice to use when generating the audio. Previews of the voices are available
     *     in the <a
     *     href="https://platform.openai.com/docs/guides/text-to-speech/voice-options">Text to
     *     speech guide</a>.
     */
    public Builder voice(Voice voice) {
      this.voice = voice.getId();
      return this;
    }

    /**
     * @param responseFormat The format to audio in
     */
    public Builder responseFormat(String responseFormat) {
      this.responseFormat = Optional.of(responseFormat);
      return this;
    }

    /**
     * @param speed The speed of the generated audio. Select a value from 0.25 to 4.0. 1.0 is the
     *     default.
     */
    public Builder speed(double speed) {
      this.speed = Optional.of(speed);
      return this;
    }

    public SpeechRequest build() {
      return new SpeechRequest(model, input, voice, responseFormat, speed);
    }
  }
}
