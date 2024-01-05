package io.github.stefanbratanov.chatjpt;

class Constants {

  private Constants() {}

  static final String JSON_MEDIA_TYPE = "application/json";
  static final String CONTENT_TYPE_HEADER = "Content-Type";
  static final String ACCEPT_HEADER = "Accept";

  static final String SYSTEM_MESSAGE_ROLE = "system";
  static final String USER_MESSAGE_ROLE = "user";
  static final String ASSISTANT_MESSAGE_ROLE = "assistant";
  static final String TOOL_MESSAGE_ROLE = "tool";
}
