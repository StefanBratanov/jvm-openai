package io.github.stefanbratanov.chatjpt;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ChatJPTIntegrationTestBase {

  protected static ChatJPT chatJPT;

  @BeforeAll
  public static void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    chatJPT = ChatJPT.newBuilder(apiKey).build();
  }

  @AfterAll
  public static void cleanUp() {
    // Cleanup of files uploads
    FilesClient filesClient = chatJPT.filesClient();
    filesClient
        .listFiles()
        .forEach(
            file -> {
              try {
                DeletionStatus deletionStatus = filesClient.deleteFile(file.id());
                assertThat(deletionStatus.deleted()).isTrue();
              } catch (OpenAIException ex) {
                assertThat(ex.statusCode()).isEqualTo(409);
                assertThat(ex.errorMessage())
                    .isEqualTo("File is still processing. Check back later.");
              }
            });
  }

  protected static Path getTestResource(String resource) {
    try {
      return Paths.get(
          Objects.requireNonNull(ChatJPTIntegrationTest.class.getResource(resource)).toURI());
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
}
