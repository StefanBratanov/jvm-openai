package io.github.stefanbratanov.jvm.openai;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestUtil {

  public static Path getTestResource(String resource) {
    try {
      return Paths.get(Objects.requireNonNull(TestUtil.class.getResource(resource)).toURI());
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String getStringResource(String resource) {
    try {
      return Files.readString(getTestResource(resource));
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
