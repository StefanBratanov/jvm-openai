package io.github.stefanbratanov.chatjpt;

import java.net.URL;
import java.util.List;

public record Images(long created, List<Image> data) {

  public record Image(String b64Json, URL url, String revisedPrompt) {}
}
