package io.github.stefanbratanov.chatjpt;

enum Endpoint {
  CHAT("chat/completions"),
  MODELS("models"),
  SPEECH("audio/speech"),
  TRANSCRIPTION("audio/transcriptions"),
  TRANSLATION("audio/translations"),
  IMAGE_GENERATION("images/generations"),
  IMAGE_EDIT("images/edits"),
  IMAGE_VARIATION("images/variations"),
  MODERATIONS("moderations"),
  EMBEDDINCS("embeddings"),
  FILES("files");

  private final String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }
}
