package io.github.stefanbratanov.chatjpt;

import java.util.List;

public record FineTuningJob(
    String id,
    long createdAt,
    Error error,
    String fineTunedModel,
    Long finishedAt,
    Hyperparameters hyperparameters,
    String model,
    String organizationId,
    List<String> resultFiles,
    String status,
    Integer trainedTokens,
    String trainingFile,
    String validationFile) {

  public record Error(String code, String message, String param) {}

  /**
   * The hyperparameters used for the fine-tuning job
   *
   * @param nEpochs {@link String} or {@link Integer}
   */
  public record Hyperparameters(Object nEpochs) {}
}
