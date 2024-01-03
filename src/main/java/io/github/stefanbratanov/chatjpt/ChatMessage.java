package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ChatMessage.SystemMessage.class, name = Constants.SYSTEM_MESSAGE_ROLE),
  @JsonSubTypes.Type(value = ChatMessage.UserMessage.class, name = Constants.USER_MESSAGE_ROLE),
  @JsonSubTypes.Type(
      value = ChatMessage.AssistantMessage.class,
      name = Constants.ASSISTANT_MESSAGE_ROLE),
  @JsonSubTypes.Type(value = ChatMessage.ToolMessage.class, name = Constants.TOOL_MESSAGE_ROLE)
})
public sealed interface ChatMessage
    permits ChatMessage.SystemMessage,
        ChatMessage.UserMessage,
        ChatMessage.AssistantMessage,
        ChatMessage.ToolMessage {

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  String role();

  String content();

  record SystemMessage(String content, Optional<String> name) implements ChatMessage {
    @Override
    public String role() {
      return Constants.SYSTEM_MESSAGE_ROLE;
    }
  }

  record UserMessage(String content, Optional<String> name) implements ChatMessage {
    @Override
    public String role() {
      return Constants.USER_MESSAGE_ROLE;
    }
  }

  record AssistantMessage(String content, Optional<String> name, Optional<List<ToolCall>> toolCalls)
      implements ChatMessage {
    @Override
    public String role() {
      return Constants.ASSISTANT_MESSAGE_ROLE;
    }
  }

  record ToolMessage(String content, String toolCallId) implements ChatMessage {
    @Override
    public String role() {
      return Constants.TOOL_MESSAGE_ROLE;
    }
  }

  static SystemMessage systemMessage(String content) {
    return new SystemMessage(content, Optional.empty());
  }

  static UserMessage userMessage(String content) {
    return new UserMessage(content, Optional.empty());
  }
}
