package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.stefanbratanov.jvm.openai.ModerationRequest.Builder.MultiModalInput.ImageUrlInput;
import io.github.stefanbratanov.jvm.openai.ModerationRequest.Builder.MultiModalInput.ImageUrlInput.ImageUrl;
import io.github.stefanbratanov.jvm.openai.ModerationRequest.Builder.MultiModalInput.TextInput;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record ModerationRequest(List<Object> input, Optional<String> model) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<Object> input = new LinkedList<>();

    private Optional<String> model = Optional.empty();

    /**
     * @param input a string of text to append to the list of inputs to classify for moderation
     */
    public Builder input(String input) {
      this.input.add(input);
      return this;
    }

    /**
     * @param inputs an array of strings to append to the list of inputs to classify for moderation
     */
    public Builder inputs(List<String> inputs) {
      input.addAll(inputs);
      return this;
    }

    /**
     * @param input multi-modal input to append to the list of inputs to classify for moderation
     */
    public Builder multiModalInput(MultiModalInput input) {
      this.input.add(input);
      return this;
    }

    /**
     * @param inputs an array of multi-modal inputs to append to the list of inputs to classify for
     *     moderation
     */
    public Builder multiModalInputs(List<MultiModalInput> inputs) {
      this.input.addAll(inputs);
      return this;
    }

    /**
     * @param model The content moderation model you would like to use.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    /**
     * @param model The content moderation {@link OpenAIModel} you would like to use.
     */
    public Builder model(OpenAIModel model) {
      this.model = Optional.of(model.getId());
      return this;
    }

    public sealed interface MultiModalInput permits ImageUrlInput, TextInput {

      @JsonProperty(access = JsonProperty.Access.READ_ONLY)
      String type();

      record ImageUrlInput(ImageUrl imageUrl) implements MultiModalInput {

        @Override
        public String type() {
          return "image_url";
        }

        public record ImageUrl(String url) {}
      }

      record TextInput(String text) implements MultiModalInput {

        @Override
        public String type() {
          return "text";
        }
      }

      static ImageUrlInput imageUrl(ImageUrl imageUrl) {
        return new ImageUrlInput(imageUrl);
      }

      static TextInput text(String text) {
        return new TextInput(text);
      }
    }

    public ModerationRequest build() {
      return new ModerationRequest(List.copyOf(input), model);
    }
  }
}
