package io.github.stefanbratanov.jvm.openai;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record EmbeddingsRequest(
    List<Object> input,
    String model,
    Optional<String> encodingFormat,
    Optional<Integer> dimensions,
    Optional<String> user) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private List<Object> input;
    private String model;
    private Optional<String> encodingFormat = Optional.empty();
    private Optional<Integer> dimensions = Optional.empty();
    private Optional<String> user = Optional.empty();

    /**
     * @param input The string(s) that will be turned into an embedding.
     */
    public Builder input(String... input) {
      this.input = Arrays.asList(input);
      return this;
    }

    /**
     * @param input The array of integers that will be turned into an embedding.
     */
    public Builder input(int[] input) {
      this.input = List.of(input);
      return this;
    }

    /**
     * @param input The array of arrays containing integers that will be turned into an embedding.
     */
    public Builder input(List<int[]> input) {
      this.input = List.copyOf(input);
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
     * @param model {@link OpenAIModel} to use
     */
    public Builder model(OpenAIModel model) {
      this.model = model.getId();
      return this;
    }

    /**
     * @param encodingFormat The format to return the embeddings in. Can be either float or base64.
     */
    public Builder encodingFormat(String encodingFormat) {
      this.encodingFormat = Optional.of(encodingFormat);
      return this;
    }

    /**
     * @param dimensions The number of dimensions the resulting output embeddings should have. Only
     *     supported in text-embedding-3 and later models.
     */
    public Builder dimensions(int dimensions) {
      this.dimensions = Optional.of(dimensions);
      return this;
    }

    /**
     * @param user A unique identifier representing your end-user, which can help OpenAI to monitor
     *     and detect abuse.
     */
    public Builder user(String user) {
      this.user = Optional.of(user);
      return this;
    }

    public EmbeddingsRequest build() {
      return new EmbeddingsRequest(input, model, encodingFormat, dimensions, user);
    }
  }
}
