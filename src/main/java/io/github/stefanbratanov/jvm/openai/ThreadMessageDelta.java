package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/** Represents a message delta i.e. any changed fields on a message during streaming. */
public record ThreadMessageDelta(String id, Delta delta) implements AssistantStreamEvent.Data {

  /** The delta containing the fields that have changed on the Message. */
  public record Delta(String role, List<Content> content) {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
      @JsonSubTypes.Type(
          value = Content.ImageFileContent.class,
          name = Constants.IMAGE_FILE_MESSAGE_CONTENT_TYPE),
      @JsonSubTypes.Type(
          value = Content.TextContent.class,
          name = Constants.TEXT_MESSAGE_CONTENT_TYPE),
    })
    public sealed interface Content permits Content.ImageFileContent, Content.TextContent {
      /** The index of the content part in the message. */
      int index();

      @JsonProperty(access = JsonProperty.Access.READ_ONLY)
      String type();

      record ImageFileContent(int index, ImageFile imageFile) implements Content {
        @Override
        public String type() {
          return Constants.IMAGE_FILE_MESSAGE_CONTENT_TYPE;
        }

        public record ImageFile(String fileId) {}
      }

      record TextContent(int index, Text text) implements Content {
        @Override
        public String type() {
          return Constants.TEXT_MESSAGE_CONTENT_TYPE;
        }

        public record Text(String value, List<Annotation> annotations) {

          @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
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
            /** The index of the annotation in the text content part. */
            int index();

            @JsonProperty(access = JsonProperty.Access.READ_ONLY)
            String type();

            String text();

            int startIndex();

            int endIndex();

            record FileCitationAnnotation(
                int index, String text, FileCitation fileCitation, int startIndex, int endIndex)
                implements Annotation {
              @Override
              public String type() {
                return Constants.FILE_CITATION_ANNOTATION_TYPE;
              }

              public record FileCitation(String fileId, String quote) {}
            }

            record FilePathAnnotation(
                int index, String text, FilePath filePath, int startIndex, int endIndex)
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
}
