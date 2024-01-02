package io.github.stefanbratanov.chatjpt;

import java.nio.file.Path;
import java.util.Optional;

public record TranslationRequest(
    Path file, String model, Optional<String> prompt, Optional<Double> temperature) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = "whisper-1";

    private Path file;
    private String model = DEFAULT_MODEL;
    private Optional<String> prompt = Optional.empty();
    private Optional<Double> temperature = Optional.empty();

    /**
     * @param file The audio file object (not file name) translate, in one of these formats: flac,
     *     mp3, mp4, mpeg, mpga, m4a, ogg, wav, or webm.
     */
    public Builder file(Path file) {
      this.file = file;
      return this;
    }

    /**
     * @param model ID of the model to use
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param prompt An optional text to guide the model's style or continue a previous audio
     *     segment. The <a
     *     href="https://platform.openai.com/docs/guides/speech-to-text/prompting">prompt</a> should
     *     match the audio language.
     */
    public Builder prompt(String prompt) {
      this.prompt = Optional.of(prompt);
      return this;
    }

    /**
     * @param temperature The sampling temperature, between 0 and 1. Higher values like 0.8 will
     *     make the output more random, while lower values like 0.2 will make it more focused and
     *     deterministic. If set to 0, the model will use <a
     *     href="https://en.wikipedia.org/wiki/Log_probability">log probability</a> to automatically
     *     increase the temperature until certain thresholds are hit.
     */
    public Builder temperature(double temperature) {
      if (temperature < 0 || temperature > 1) {
        throw new IllegalArgumentException(
            "temperature must be between 0 and 1 but it was " + temperature);
      }
      this.temperature = Optional.of(temperature);
      return this;
    }

    public TranslationRequest build() {
      if (file == null) {
        throw new IllegalStateException("file must be set");
      }
      return new TranslationRequest(file, model, prompt, temperature);
    }
  }
}
