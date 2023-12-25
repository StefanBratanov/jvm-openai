package io.github.stefanbratanov.chatjpt;

public record Message(String role, String content) {

  public static Message systemMessage(String content) {
    return new Message("system", content);
  }

  public static Message userMessage(String content) {
    return new Message("user", content);
  }

  public static Message assistantMessage(String content) {
    return new Message("assistant", content);
  }

  public static Message toolMessage(String content) {
    return new Message("tool", content);
  }
}
