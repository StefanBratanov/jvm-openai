package io.github.stefanbratanov.chatjpt;

import java.util.Optional;

public record ModerationRequest(String input, Optional<String> model) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String input;
    private Optional<String> model = Optional.empty();

    /**
     * @param input The input text to classify
     */
    public Builder input(String input) {
      this.input = input;
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
      if (input == null) {
        throw new IllegalStateException("input must be set");
      }
      return new ModerationRequest(input, model);
    }
  }
}
