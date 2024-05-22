package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @param content {@link String} or a {@link List<ContentPart>}
 */
public record CreateMessageRequest(
    String role,
    Object content,
    Optional<List<Attachment>> attachments,
    Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String role = Role.USER.getId();

    private Object content;
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
     * @param content An array of content parts with a defined type
     */
    public Builder content(List<ContentPart> content) {
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
     *     can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    public CreateMessageRequest build() {
      return new CreateMessageRequest(role, content, attachments, metadata);
    }
  }
}
