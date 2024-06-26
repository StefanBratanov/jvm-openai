package io.github.stefanbratanov.jvm.openai;

import static io.github.stefanbratanov.jvm.openai.TestConstants.OPEN_AI_SPECIFICATION_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.mock.OpenAPIExpectation.openAPIExpectation;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.event.Level;

public class OpenAIIntegrationTestBase {

  protected static OpenAI openAI;

  private static ClientAndServer mockServer;
  protected static OpenAI openAIWithMockServer;

  @BeforeAll
  public static void setUp() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    openAI = OpenAI.newBuilder(apiKey).build();
    mockServer =
        ClientAndServer.startClientAndServer(Configuration.configuration().logLevel(Level.WARN));
    mockServer.upsert(openAPIExpectation(OPEN_AI_SPECIFICATION_URL));
    openAIWithMockServer =
        OpenAI.newBuilder(apiKey).baseUrl("http://localhost:" + mockServer.getPort()).build();
  }

  @AfterAll
  public static void cleanUp() {
    // Cleanup of vector stores
    VectorStoresClient vectorStoresClient = openAI.vectorStoresClient();
    vectorStoresClient
        .listVectorStores(PaginationQueryParameters.none())
        .data()
        .forEach(
            vectorStore -> {
              DeletionStatus deletionStatus =
                  vectorStoresClient.deleteVectorStore(vectorStore.id());
              assertThat(deletionStatus.deleted()).isTrue();
            });
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
    mockServer.stop();
  }

  protected void awaitCondition(
      Supplier<Boolean> condition, Duration pollingInterval, Duration timeout) {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    AtomicReference<Throwable> conditionFailure = new AtomicReference<>();
    CountDownLatch conditionSatisfiedLatch = new CountDownLatch(1);
    executor.scheduleAtFixedRate(
        () -> {
          try {
            if (condition.get()) {
              conditionSatisfiedLatch.countDown();
            }
          } catch (Throwable ex) {
            conditionFailure.set(ex);
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
      Throwable failure = conditionFailure.get();
      if (failure != null) {
        Assertions.fail("Exception in condition check", failure);
      }
    } catch (InterruptedException ex) {
      Assertions.fail("The await was interrupted", ex);
    } finally {
      executor.shutdown();
    }
  }
}
