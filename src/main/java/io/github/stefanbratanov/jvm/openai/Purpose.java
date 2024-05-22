package io.github.stefanbratanov.jvm.openai;

/** The intended purpose of a file */
public enum Purpose {
  ASSISTANTS("assistants"),
  BATCH("batch"),
  FINE_TUNE("fine-tune"),
  VISION("vision");

  private final String id;

  Purpose(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
