package io.github.stefanbratanov.chatjpt;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateThreadRequest(
    Optional<List<ThreadMessage>> messages, Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private final List<ThreadMessage> messages = new LinkedList<>();

    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param message message to append to the list of messages to start the thread with.
     */
    public Builder message(ThreadMessage message) {
      messages.add(message);
      return this;
    }

    /**
     * @param messages messages to append to the list of messages to start the thread with.
     */
    public Builder messages(List<ThreadMessage> messages) {
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
          messages.isEmpty() ? Optional.empty() : Optional.of(messages), metadata);
    }
  }
}
