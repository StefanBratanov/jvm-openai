package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.stefanbratanov.jvm.openai.ChatMessage.UserMessage.UserMessageWithContentParts;
import io.github.stefanbratanov.jvm.openai.ChatMessage.UserMessage.UserMessageWithTextContent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public sealed interface ChatMessage
    permits ChatMessage.SystemMessage,
        ChatMessage.UserMessage,
        ChatMessage.AssistantMessage,
        ChatMessage.ToolMessage {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String role();

  record SystemMessage(String content, Optional<String> name) implements ChatMessage {
    @Override
    public String role() {
      return Role.SYSTEM.getId();
    }
  }

  sealed interface UserMessage<T> extends ChatMessage
      permits UserMessageWithTextContent, UserMessageWithContentParts {
    @Override
    default String role() {
      return Role.USER.getId();
    }

    T content();

    record UserMessageWithTextContent(String content, Optional<String> name)
        implements UserMessage<String> {}

    record UserMessageWithContentParts(List<ContentPart> content, Optional<String> name)
        implements UserMessage<List<ContentPart>> {}
  }

  record AssistantMessage(
      String content,
      Optional<String> refusal,
      Optional<String> name,
      Optional<List<ToolCall>> toolCalls)
      implements ChatMessage {
    @Override
    public String role() {
      return Role.ASSISTANT.getId();
    }
  }

  record ToolMessage(String content, String toolCallId) implements ChatMessage {
    @Override
    public String role() {
      return Role.TOOL.getId();
    }
  }

  static SystemMessage systemMessage(String content) {
    return new SystemMessage(content, Optional.empty());
  }

  static UserMessageWithTextContent userMessage(String content) {
    return new UserMessageWithTextContent(content, Optional.empty());
  }

  static UserMessageWithContentParts userMessage(ContentPart... content) {
    return new UserMessageWithContentParts(Arrays.asList(content), Optional.empty());
  }

  static AssistantMessage assistantMessage(String content) {
    return new AssistantMessage(content, Optional.empty(), Optional.empty(), Optional.empty());
  }

  static AssistantMessage assistantMessage(String content, List<ToolCall> toolCalls) {
    return new AssistantMessage(
        content, Optional.empty(), Optional.empty(), Optional.of(toolCalls));
  }

  static AssistantMessage assistantMessage(
      String content, String refusal, List<ToolCall> toolCalls) {
    return new AssistantMessage(
        content, Optional.of(refusal), Optional.empty(), Optional.of(toolCalls));
  }

  static ToolMessage toolMessage(String content, String toolCallId) {
    return new ToolMessage(content, toolCallId);
  }
}
