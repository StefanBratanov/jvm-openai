package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record CreateFineTuningJobRequest(
    String model,
    String trainingFile,
    Optional<Hyperparameters> hyperparameters,
    Optional<String> suffix,
    Optional<String> validationFile) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public record Hyperparameters(
      Optional<Object> batchSize,
      Optional<Object> learningRateMultiplier,
      Optional<Object> nEpochs) {

    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {

      private Optional<Object> batchSize = Optional.empty();
      private Optional<Object> learningRateMultiplier = Optional.empty();
      private Optional<Object> nEpochs = Optional.empty();

      /**
       * @param batchSize (string or integer) Number of examples in each batch. A larger batch size
       *     means that model parameters are updated less frequently, but with lower variance.
       */
      public Builder batchSize(String batchSize) {
        this.batchSize = Optional.of(batchSize);
        return this;
      }

      /**
       * @param batchSize (string or integer) Number of examples in each batch. A larger batch size
       *     means that model parameters are updated less frequently, but with lower variance.
       */
      public Builder batchSize(Integer batchSize) {
        this.batchSize = Optional.of(batchSize);
        return this;
      }

      /**
       * @param learningRateMultiplier (string or integer) Scaling factor for the learning rate. A
       *     smaller learning rate may be useful to avoid overfitting.
       */
      public Builder learningRateMultiplier(String learningRateMultiplier) {
        this.learningRateMultiplier = Optional.of(learningRateMultiplier);
        return this;
      }

      /**
       * @param learningRateMultiplier (string or integer) Scaling factor for the learning rate. A
       *     smaller learning rate may be useful to avoid overfitting.
       */
      public Builder learningRateMultiplier(Integer learningRateMultiplier) {
        this.learningRateMultiplier = Optional.of(learningRateMultiplier);
        return this;
      }

      /**
       * @param nEpochs (string or integer) The number of epochs to train the model for. An epoch
       *     refers to one full cycle through the training dataset.
       */
      public Builder nEpochs(String nEpochs) {
        this.nEpochs = Optional.of(nEpochs);
        return this;
      }

      /**
       * @param nEpochs (string or integer) The number of epochs to train the model for. An epoch
       *     refers to one full cycle through the training dataset.
       */
      public Builder nEpochs(Integer nEpochs) {
        this.nEpochs = Optional.of(nEpochs);
        return this;
      }

      public Hyperparameters build() {
        return new Hyperparameters(batchSize, learningRateMultiplier, nEpochs);
      }
    }
  }

  public static class Builder {

    private String model;
    private String trainingFile;
    private Optional<Hyperparameters> hyperparameters = Optional.empty();
    private Optional<String> suffix = Optional.empty();
    private Optional<String> validationFile = Optional.empty();

    /**
     * @param model The name of the model to fine-tune
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param trainingFile The ID of an uploaded file that contains training data. Your dataset must
     *     be formatted as a JSONL file. Additionally, you must upload your file with the purpose
     *     fine-tune.
     */
    public Builder trainingFile(String trainingFile) {
      this.trainingFile = trainingFile;
      return this;
    }

    /**
     * @param hyperparameters The hyperparameters used for the fine-tuning job
     */
    public Builder hyperparameters(Hyperparameters hyperparameters) {
      this.hyperparameters = Optional.of(hyperparameters);
      return this;
    }

    /**
     * @param suffix A string of up to 18 characters that will be added to your fine-tuned model
     *     name
     */
    public Builder suffix(String suffix) {
      this.suffix = Optional.of(suffix);
      return this;
    }

    /**
     * @param validationFile The ID of an uploaded file that contains validation data.
     *     <p>If you provide this file, the data is used to generate validation metrics periodically
     *     during fine-tuning. These metrics can be viewed in the fine-tuning results file. The same
     *     data should not be present in both train and validation files.
     */
    public Builder validationFile(String validationFile) {
      this.validationFile = Optional.of(validationFile);
      return this;
    }

    public CreateFineTuningJobRequest build() {
      return new CreateFineTuningJobRequest(
          model, trainingFile, hyperparameters, suffix, validationFile);
    }
  }
}
