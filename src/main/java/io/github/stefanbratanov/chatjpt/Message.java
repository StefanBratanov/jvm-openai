package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Message.SystemMessage.class, name = "system"),
  @JsonSubTypes.Type(value = Message.UserMessage.class, name = "user"),
  @JsonSubTypes.Type(value = Message.AssistantMessage.class, name = "assistant"),
  @JsonSubTypes.Type(value = Message.ToolMessage.class, name = "tool")
})
public sealed interface Message
    permits Message.SystemMessage,
        Message.UserMessage,
        Message.AssistantMessage,
        Message.ToolMessage {

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  String role();

  String content();

  record SystemMessage(String content, Optional<String> name) implements Message {
    @Override
    public String role() {
      return "system";
    }
  }

  record UserMessage(String content, Optional<String> name) implements Message {
    @Override
    public String role() {
      return "user";
    }
  }

  record AssistantMessage(String content, Optional<String> name, Optional<List<ToolCall>> toolCalls)
      implements Message {
    @Override
    public String role() {
      return "assistant";
    }

    public record ToolCall(String id, String type, Function function) {
      public record Function(String name, String arguments) {}
    }
  }

  record ToolMessage(String content, String toolCallId) implements Message {
    @Override
    public String role() {
      return "tool";
    }
  }

  static Message systemMessage(String content) {
    return new SystemMessage(content, Optional.empty());
  }

  static Message userMessage(String content) {
    return new UserMessage(content, Optional.empty());
  }
}
