package io.github.stefanbratanov.chatjpt;

enum Endpoint {
  CHAT("chat/completions"),
  MODELS("models"),
  SPEECH("audio/speech"),
  TRANSCRIPTIONS("audio/transcriptions");

  private final String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }
}
