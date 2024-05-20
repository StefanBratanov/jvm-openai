package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.stefanbratanov.jvm.openai.ContentPart.ImageFileContentPart.ImageFile;
import io.github.stefanbratanov.jvm.openai.ContentPart.ImageUrlContentPart.ImageUrl;
import java.util.Optional;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = ContentPart.TextContentPart.class,
      name = Constants.TEXT_CONTENT_PART_TYPE),
  @JsonSubTypes.Type(
      value = ContentPart.ImageUrlContentPart.class,
      name = Constants.IMAGE_URL_CONTENT_PART_TYPE),
  @JsonSubTypes.Type(
      value = ContentPart.ImageFileContentPart.class,
      name = Constants.IMAGE_FILE_CONTENT_PART_TYPE),
})
public sealed interface ContentPart
    permits ContentPart.TextContentPart,
        ContentPart.ImageUrlContentPart,
        ContentPart.ImageFileContentPart {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record TextContentPart(String text) implements ContentPart {
    @Override
    public String type() {
      return Constants.TEXT_CONTENT_PART_TYPE;
    }
  }

  record ImageUrlContentPart(ImageUrl imageUrl) implements ContentPart {
    @Override
    public String type() {
      return Constants.IMAGE_URL_CONTENT_PART_TYPE;
    }

    public record ImageUrl(String url, Optional<String> detail) {}
  }

  record ImageFileContentPart(ImageFile imageFile) implements ContentPart {
    @Override
    public String type() {
      return Constants.IMAGE_FILE_CONTENT_PART_TYPE;
    }

    public record ImageFile(String fileId, Optional<String> detail) {}
  }

  static TextContentPart textContentPart(String text) {
    return new TextContentPart(text);
  }

  static ImageUrlContentPart imageUrlContentPart(String url) {
    return new ImageUrlContentPart(new ImageUrl(url, Optional.empty()));
  }

  static ImageUrlContentPart imageUrlContentPart(String url, String detail) {
    return new ImageUrlContentPart(new ImageUrl(url, Optional.of(detail)));
  }

  static ImageFileContentPart imageFileContentPart(String fileId) {
    return new ImageFileContentPart(new ImageFile(fileId, Optional.empty()));
  }

  static ImageFileContentPart imageFileContentPart(String fileId, String detail) {
    return new ImageFileContentPart(new ImageFile(fileId, Optional.of(detail)));
  }
}
