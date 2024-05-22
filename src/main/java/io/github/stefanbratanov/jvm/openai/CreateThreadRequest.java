package io.github.stefanbratanov.jvm.openai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateThreadRequest(
    Optional<List<Message>> messages,
    Optional<ToolResources> toolResources,
    Optional<Map<String, String>> metadata) {

  public record Message(
      String role,
      String content,
      Optional<List<Attachment>> attachments,
      Optional<Map<String, String>> metadata) {

    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {

      private String role = Role.USER.getId();

      private String content;
      private Optional<List<Attachment>> attachments = Optional.empty();
      private Optional<Map<String, String>> metadata = Optional.empty();

      /**
       * @param role The role of the entity that is creating the message.
       */
      public Builder role(String role) {
        this.role = role;
        return this;
      }

      /**
       * @param role The role of the entity that is creating the message.
       */
      public Builder role(Role role) {
        this.role = role.getId();
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
       * @param attachments A list of files attached to the message, and the tools they should be
       *     added to.
       */
      public Builder attachments(List<Attachment> attachments) {
        this.attachments = Optional.of(attachments);
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
        return new Message(role, content, attachments, metadata);
      }
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<Message> messages = new LinkedList<>();

    private Optional<ToolResources> toolResources = Optional.empty();
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
     * @param toolResources A set of resources that are made available to the assistant's tools in
     *     this thread. The resources are specific to the type of tool. For example, the
     *     code_interpreter tool requires a list of file IDs, while the file_search tool requires a
     *     list of vector store IDs.
     */
    public Builder toolResources(ToolResources toolResources) {
      this.toolResources = Optional.of(toolResources);
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
          messages.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(messages)),
          toolResources,
          metadata);
    }
  }
}
