package io.github.stefanbratanov.jvm.openai;

import java.nio.file.Path;
import java.util.Optional;

public record EditImageRequest(
    Path image,
    String prompt,
    Optional<Path> mask,
    Optional<String> model,
    Optional<Integer> n,
    Optional<String> size,
    Optional<String> responseFormat,
    Optional<String> user) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Path image;
    private String prompt;
    private Optional<Path> mask = Optional.empty();
    private Optional<String> model = Optional.empty();
    private Optional<Integer> n = Optional.empty();
    private Optional<String> size = Optional.empty();
    private Optional<String> responseFormat = Optional.empty();
    private Optional<String> user = Optional.empty();

    /**
     * @param image The image to edit. Must be a valid PNG file, less than 4MB, and square. If mask
     *     is not provided, image must have transparency, which will be used as the mask.
     */
    public Builder image(Path image) {
      this.image = image;
      return this;
    }

    /**
     * @param prompt A text description of the desired image(s). The maximum length is 1000
     *     characters.
     */
    public Builder prompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    /**
     * @param mask An additional image whose fully transparent areas (e.g. where alpha is zero)
     *     indicate where image should be edited. Must be a valid PNG file, less than 4MB, and have
     *     the same dimensions as image.
     */
    public Builder mask(Path mask) {
      this.mask = Optional.of(mask);
      return this;
    }

    /**
     * @param model The model to use for image generation. Only dall-e-2 is supported at this time.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    /**
     * @param model {@link OpenAIModel} to use for image generation. Only {@link
     *     OpenAIModel#DALL_E_2} is supported at this time.
     */
    public Builder model(OpenAIModel model) {
      this.model = Optional.of(model.getId());
      return this;
    }

    /**
     * @param n The number of images to generate. Must be between 1 and 10.
     */
    public Builder n(int n) {
      this.n = Optional.of(n);
      return this;
    }

    /**
     * @param size The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     */
    public Builder size(String size) {
      this.size = Optional.of(size);
      return this;
    }

    /**
     * @param responseFormat The format in which the generated images are returned. Must be one of
     *     url or b64_json.
     */
    public Builder responseFormat(String responseFormat) {
      this.responseFormat = Optional.of(responseFormat);
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

    public EditImageRequest build() {
      return new EditImageRequest(image, prompt, mask, model, n, size, responseFormat, user);
    }
  }
}
