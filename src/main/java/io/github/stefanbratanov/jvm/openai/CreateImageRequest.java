package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record CreateImageRequest(
    String prompt,
    Optional<String> model,
    Optional<Integer> n,
    Optional<String> quality,
    Optional<String> responseFormat,
    Optional<String> size,
    Optional<String> style,
    Optional<String> user) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String prompt;
    private Optional<String> model = Optional.empty();
    private Optional<Integer> n = Optional.empty();
    private Optional<String> quality = Optional.empty();
    private Optional<String> responseFormat = Optional.empty();
    private Optional<String> size = Optional.empty();
    private Optional<String> style = Optional.empty();
    private Optional<String> user = Optional.empty();

    /**
     * @param prompt A text description of the desired image(s). The maximum length is 1000
     *     characters for dall-e-2 and 4000 characters for dall-e-3.
     */
    public Builder prompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    /**
     * @param model The model to use for image generation.
     */
    public Builder model(String model) {
      this.model = Optional.of(model);
      return this;
    }

    /**
     * @param n The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1
     *     is supported.
     */
    public Builder n(int n) {
      this.n = Optional.of(n);
      return this;
    }

    /**
     * @param quality The quality of the image that will be generated. hd creates images with finer
     *     details and greater consistency across the image. This param is only supported for
     *     dall-e-3.
     */
    public Builder quality(String quality) {
      this.quality = Optional.of(quality);
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
     * @param size The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024
     *     for dall-e-2. Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
     */
    public Builder size(String size) {
      this.size = Optional.of(size);
      return this;
    }

    /**
     * @param style The style of the generated images. Must be one of vivid or natural. Vivid causes
     *     the model to lean towards generating hyper-real and dramatic images. Natural causes the
     *     model to produce more natural, less hyper-real looking images. This param is only
     *     supported for dall-e-3.
     */
    public Builder style(String style) {
      this.style = Optional.of(style);
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

    public CreateImageRequest build() {
      if (prompt == null) {
        throw new IllegalStateException("prompt must be set");
      }
      return new CreateImageRequest(prompt, model, n, quality, responseFormat, size, style, user);
    }
  }
}
