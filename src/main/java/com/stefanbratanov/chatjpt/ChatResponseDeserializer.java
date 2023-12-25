package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

class ChatResponseDeserializer extends StdDeserializer<ChatResponse> {

  ChatResponseDeserializer() {
    super(ChatResponse.class);
  }

  @Override
  public ChatResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.readValueAsTree();

    String id = node.get("id").asText();
    long created = node.get("created").asLong();
    String model = node.get("model").asText();
    String systemFingerprint = node.get("system_fingerprint").asText();

    // Using the first choice because always using the default value of n (1)
    JsonNode messageNode = node.get("choices").get(0).get("message");
    String role = messageNode.get("role").asText();
    String content = messageNode.get("content").asText();

    Message message = new Message(role, content);

    JsonNode usageNode = node.get("usage");
    int promptTokens = usageNode.get("prompt_tokens").asInt();
    int completionTokens = usageNode.get("completion_tokens").asInt();
    int totalTokens = usageNode.get("total_tokens").asInt();

    Usage usage = new Usage(promptTokens, completionTokens, totalTokens);

    return new ChatResponse(id, created, model, systemFingerprint, message, usage);
  }
}
