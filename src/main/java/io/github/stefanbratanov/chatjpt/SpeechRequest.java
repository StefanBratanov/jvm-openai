package io.github.stefanbratanov.chatjpt;

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

    private static final String DEFAULT_MODEL = "tts-1";
    private static final String DEFAULT_VOICE = "alloy";

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
      if (speed < 0.25 || speed > 4) {
        throw new IllegalArgumentException(
            "speed should be between 0.25 and 4.0 but it was " + speed);
      }
      this.speed = Optional.of(speed);
      return this;
    }

    public SpeechRequest build() {
      if (input == null) {
        throw new IllegalArgumentException("input must be set");
      }
      return new SpeechRequest(model, input, voice, responseFormat, speed);
    }
  }
}
