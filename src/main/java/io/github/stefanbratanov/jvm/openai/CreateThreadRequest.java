package io.github.stefanbratanov.jvm.openai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateThreadRequest(
    Optional<List<Message>> messages, Optional<Map<String, String>> metadata) {

  public record Message(
      String role,
      String content,
      Optional<List<String>> fileIds,
      Optional<Map<String, String>> metadata) {

    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {

      private String role = Constants.USER_MESSAGE_ROLE;

      private String content;
      private Optional<List<String>> fileIds = Optional.empty();
      private Optional<Map<String, String>> metadata = Optional.empty();

      /**
       * @param role The role of the entity that is creating the message. Currently only user is
       *     supported.
       */
      public Builder role(String role) {
        this.role = role;
        return this;
      }

      /**
       * @param content The content of the message.
       */
      public Builder content(String content) {
        this.content = content;
        return this;
      }

      /**
       * @param fileIds A list of File IDs that the message should use. There can be a maximum of 10
       *     files attached to a message. Useful for tools like retrieval and code_interpreter that
       *     can access and use files.
       */
      public Builder fileIds(List<String> fileIds) {
        this.fileIds = Optional.of(fileIds);
        return this;
      }

      /**
       * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be
       *     useful for storing additional information about the object in a structured format. Keys
       *     can be a maximum of 64 characters long and values can be a maxium of 512 characters
       *     long.
       */
      public Builder metadata(Map<String, String> metadata) {
        this.metadata = Optional.of(metadata);
        return this;
      }

      public Message build() {
        if (content == null) {
          throw new IllegalStateException("content must be set");
        }
        return new Message(role, content, fileIds, metadata);
      }
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<Message> messages = new LinkedList<>();

    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param message message to append to the list of messages to start the thread with.
     */
    public Builder message(Message message) {
      messages.add(message);
      return this;
    }

    /**
     * @param messages messages to append to the list of messages to start the thread with.
     */
    public Builder messages(List<Message> messages) {
      this.messages.addAll(messages);
      return this;
    }

    /**
     * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be
     *     useful for storing additional information about the object in a structured format. Keys
     *     can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    public CreateThreadRequest build() {
      return new CreateThreadRequest(
          messages.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(messages)), metadata);
    }
  }
}
