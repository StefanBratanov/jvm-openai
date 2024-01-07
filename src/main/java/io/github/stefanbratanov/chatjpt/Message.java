package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface Message permits ChatMessage, CreateThreadRequest.Message {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String role();
}
