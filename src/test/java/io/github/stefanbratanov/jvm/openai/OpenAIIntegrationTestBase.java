package io.github.stefanbratanov.jvm.openai;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class OpenAIIntegrationTestBase {

  protected static OpenAI openAI;

  @BeforeAll
  public static void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    openAI = OpenAI.newBuilder(apiKey).build();
  }

  @AfterAll
  public static void cleanUp() {
    // Cleanup of files uploads
    FilesClient filesClient = openAI.filesClient();
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

  protected Path getTestResource(String resource) {
    try {
      return Paths.get(
          Objects.requireNonNull(OpenAIIntegrationTestBase.class.getResource(resource)).toURI());
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected void awaitCondition(
      Supplier<Boolean> condition, Duration pollingInterval, Duration timeout) {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    CountDownLatch conditionSatisfiedLatch = new CountDownLatch(1);
    executor.scheduleAtFixedRate(
        () -> {
          if (condition.get()) {
            conditionSatisfiedLatch.countDown();
          }
        },
        0,
        pollingInterval.toMillis(),
        TimeUnit.MILLISECONDS);

    try {
      if (!conditionSatisfiedLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
        Assertions.fail("The condition was not satisfied within the time limit.");
      }
    } catch (InterruptedException ex) {
      Assertions.fail(ex);
    } finally {
      executor.shutdown();
    }
  }
}
