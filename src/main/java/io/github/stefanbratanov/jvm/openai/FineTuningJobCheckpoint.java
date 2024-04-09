package io.github.stefanbratanov.jvm.openai;

/**
 * The fine_tuning.job.checkpoint object represents a model checkpoint for a fine-tuning job that is
 * ready to use.
 */
public record FineTuningJobCheckpoint(
    String id,
    long createdAt,
    String fineTunedModelCheckpoint,
    int stepNumber,
    Long finishedAt,
    Metrics metrics,
    String fineTuningJobId) {

  public record Metrics(
      double step,
      double trainLoss,
      double trainMeanTokenAccuracy,
      double validLoss,
      double validMeanTokenAccuracy,
      double fullValidLoss,
      double fullValidMeanTokenAccuracy) {}
}
