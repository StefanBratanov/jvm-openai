package io.github.stefanbratanov.jvm.openai;

/** The format of the output */
public enum AudioResponseFormat {
  JSON("json"),
  TEXT("text"),
  SRT("srt"),
  VERBOSE_JSON("verbose_json"),
  VTT("vtt");

  private final String id;

  AudioResponseFormat(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
