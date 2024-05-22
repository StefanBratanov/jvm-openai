package io.github.stefanbratanov.jvm.openai;

public enum Role {
  SYSTEM("system"),
  USER("user"),
  ASSISTANT("assistant"),
  TOOL("tool"),
  FUNCTION("function");

  private final String id;

  Role(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
