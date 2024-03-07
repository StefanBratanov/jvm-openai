package io.github.stefanbratanov.jvm.openai;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public record TranscriptionRequest(
    Path file,
    String model,
    Optional<String> language,
    Optional<String> prompt,
    Optional<String> responseFormat,
    Optional<Double> temperature,
    Optional<List<String>> timestampGranularities) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = "whisper-1";

    private Path file;
    private String model = DEFAULT_MODEL;
    private Optional<String> language = Optional.empty();
    private Optional<String> prompt = Optional.empty();
    private Optional<String> responseFormat = Optional.empty();
    private Optional<Double> temperature = Optional.empty();
    private Optional<List<String>> timestampGranularities = Optional.empty();

    /**
     * @param file The audio file object (not file name) to transcribe, in one of these formats:
     *     flac, mp3, mp4, mpeg, mpga, m4a, ogg, wav, or webm.
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
     * @param language The language of the input audio. Supplying the input language in <a
     *     href="https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes">ISO-639-1</a> format
     *     will improve accuracy and latency.
     */
    public Builder language(String language) {
      this.language = Optional.of(language);
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
     * @param responseFormat The format of the transcript output
     */
    public Builder responseFormat(String responseFormat) {
      this.responseFormat = Optional.of(responseFormat);
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
      this.temperature = Optional.of(temperature);
      return this;
    }

    /**
     * @param timestampGranularities The timestamp granularities to populate for this transcription.
     *     `response_format` must be set `verbose_json` to use timestamp granularities. Either or
     *     both of these options are supported: `word`, or `segment`. Note: There is no additional
     *     latency for segment timestamps, but generating word timestamps incurs additional latency.
     */
    public Builder timestampGranularities(List<String> timestampGranularities) {
      this.timestampGranularities = Optional.of(timestampGranularities);
      return this;
    }

    public TranscriptionRequest build() {
      return new TranscriptionRequest(
          file, model, language, prompt, responseFormat, temperature, timestampGranularities);
    }
  }
}
