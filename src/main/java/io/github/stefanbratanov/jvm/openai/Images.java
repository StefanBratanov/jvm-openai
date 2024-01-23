package io.github.stefanbratanov.jvm.openai;

import java.util.List;

public record Images(long created, List<Image> data) {

  public record Image(String b64Json, String url, String revisedPrompt) {}
}
