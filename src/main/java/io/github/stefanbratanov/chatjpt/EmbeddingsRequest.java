package io.github.stefanbratanov.chatjpt;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record EmbeddingsRequest(
    List<String> input, String model, Optional<String> encodingFormat, Optional<String> user) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<String> input = new LinkedList<>();

    private String model;
    private Optional<String> encodingFormat = Optional.empty();
    private Optional<String> user = Optional.empty();

    /**
     * @param input input to append to the list of input texts to embed, encoded as a string
     */
    public Builder input(String input) {
      this.input.add(input);
      return this;
    }

    /**
     * @param inputs inputs to append to the list of input texts to embed, encoded as a string
     */
    public Builder inputs(List<String> inputs) {
      input.addAll(inputs);
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
     * @param encodingFormat The format to return the embeddings in. Can be either float or base64.
     */
    public Builder encodingFormat(String encodingFormat) {
      this.encodingFormat = Optional.of(encodingFormat);
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
      if (input.isEmpty()) {
        throw new IllegalStateException("at least one input must be set");
      }
      if (model == null) {
        throw new IllegalStateException("model must be set");
      }
      return new EmbeddingsRequest(List.copyOf(input), model, encodingFormat, user);
    }
  }
}
