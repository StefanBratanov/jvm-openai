package io.github.stefanbratanov.jvm.openai;

/** The voice to use when generating an audio */
public enum Voice {
  ALLOY("alloy"),
  ECHO("echo"),
  FABLE("fable"),
  ONYX("onyx"),
  NOVA("nova"),
  SHIMMER("shimmer");

  private final String id;

  Voice(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
