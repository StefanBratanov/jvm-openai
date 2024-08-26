package io.github.stefanbratanov.jvm.openai;

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
  FILES("files"),
  FINE_TUNING("fine_tuning/jobs"),
  BATCHES("batches"),
  UPLOADS("uploads"),
  // Beta
  THREADS("threads"),
  ASSISTANTS("assistants"),
  VECTOR_STORES("vector_stores"),
  // Administration
  INVITES("organization/invites"),
  USERS("organization/users");

  private final String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }
}
