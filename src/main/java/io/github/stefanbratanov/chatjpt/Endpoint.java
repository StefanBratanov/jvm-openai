package io.github.stefanbratanov.chatjpt;

enum Endpoint {
  CHAT("chat/completions"),
  MODELS("models");

  private final String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }
}
