package io.github.stefanbratanov.jvm.openai;

import java.util.Map;

/**
 * A Thread represents a conversation. It is recommended creating one Thread per user as soon as the
 * user initiates the conversation.
 */
public record Thread(
    String id, long createdAt, ToolResources toolResources, Map<String, String> metadata)
    implements AssistantStreamEvent.Data {}
