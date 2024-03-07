package io.github.stefanbratanov.jvm.openai;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record ModerationRequest(List<String> input, Optional<String> model) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<String> input = new LinkedList<>();

    private Optional<String> model = Optional.empty();

    /**
     * @param input input to append to the list of input texts to classify
     */
    public Builder input(String input) {
      this.input.add(input);
      return this;
    }

    /**
     * @param inputs inputs to append to the list of input texts to classify
     */
    public Builder inputs(List<String> inputs) {
      input.addAll(inputs);
      return this;
    }

    /**
     * @param model Two content moderations models are available: text-moderation-stable and
     *     text-moderation-latest.
     *     <p>The default is text-moderation-latest which will be automatically upgraded over time.
     *     This ensures you are always using our most accurate model. If you use
     *     text-moderation-stable, we will provide advanced notice before updating the model.
     *     Accuracy of text-moderation-stable may be slightly lower than for text-moderation-latest.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    public ModerationRequest build() {
      return new ModerationRequest(List.copyOf(input), model);
    }
  }
}
