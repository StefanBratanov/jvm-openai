package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Represents a message within a thread. */
public record ThreadMessage(
    String id,
    long createdAt,
    String threadId,
    String status,
    IncompleteDetails incompleteDetails,
    Long completedAt,
    Long incompleteAt,
    String role,
    List<Content> content,
    String assistantId,
    String runId,
    List<Attachment> attachments,
    Map<String, String> metadata)
    implements AssistantStreamEvent.Data {

  /** On an incomplete message, details about why the message is incomplete. */
  public record IncompleteDetails(String reason) {}

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      property = "type",
      include = JsonTypeInfo.As.EXISTING_PROPERTY)
  @JsonSubTypes({
    @JsonSubTypes.Type(
        value = Content.ImageFileContent.class,
        name = Constants.IMAGE_FILE_MESSAGE_CONTENT_TYPE),
    @JsonSubTypes.Type(
        value = Content.ImageUrlContent.class,
        name = Constants.IMAGE_URL_MESSAGE_CONTENT_TYPE),
    @JsonSubTypes.Type(
        value = Content.TextContent.class,
        name = Constants.TEXT_MESSAGE_CONTENT_TYPE),
  })
  public sealed interface Content
      permits Content.ImageFileContent, Content.ImageUrlContent, Content.TextContent {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String type();

    record ImageFileContent(ImageFile imageFile) implements Content {
      @Override
      public String type() {
        return Constants.IMAGE_FILE_MESSAGE_CONTENT_TYPE;
      }

      public record ImageFile(String fileId, Optional<String> detail) {}
    }

    record ImageUrlContent(ImageUrl imageUrl) implements Content {
      @Override
      public String type() {
        return Constants.IMAGE_URL_MESSAGE_CONTENT_TYPE;
      }

      public record ImageUrl(String url, Optional<String> detail) {}
    }

    record TextContent(Text text) implements Content {
      @Override
      public String type() {
        return Constants.TEXT_MESSAGE_CONTENT_TYPE;
      }

      public record Text(String value, List<Annotation> annotations) {

        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            include = JsonTypeInfo.As.EXISTING_PROPERTY)
        @JsonSubTypes({
          @JsonSubTypes.Type(
              value = Annotation.FileCitationAnnotation.class,
              name = Constants.FILE_CITATION_ANNOTATION_TYPE),
          @JsonSubTypes.Type(
              value = Annotation.FilePathAnnotation.class,
              name = Constants.FILE_PATH_ANNOTATION_TYPE),
        })
        public sealed interface Annotation
            permits Annotation.FileCitationAnnotation, Annotation.FilePathAnnotation {
          @JsonProperty(access = JsonProperty.Access.READ_ONLY)
          String type();

          String text();

          int startIndex();

          int endIndex();

          record FileCitationAnnotation(
              String text, FileCitation fileCitation, int startIndex, int endIndex)
              implements Annotation {
            @Override
            public String type() {
              return Constants.FILE_CITATION_ANNOTATION_TYPE;
            }

            public record FileCitation(String fileId) {}
          }

          record FilePathAnnotation(String text, FilePath filePath, int startIndex, int endIndex)
              implements Annotation {
            @Override
            public String type() {
              return Constants.FILE_PATH_ANNOTATION_TYPE;
            }

            public record FilePath(String fileId) {}
          }
        }
      }
    }
  }
}
