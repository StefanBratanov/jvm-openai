package io.github.stefanbratanov.jvm.openai;

import java.util.Arrays;
import java.util.List;

public record Attachment(String fileId, List<Tool> tools) {

  public static Attachment of(String fileId, Tool... tools) {
    return new Attachment(fileId, Arrays.asList(tools));
  }
}
