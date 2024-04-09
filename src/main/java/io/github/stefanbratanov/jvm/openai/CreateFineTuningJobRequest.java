package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CreateFineTuningJobRequest(
    String model,
    String trainingFile,
    Optional<Hyperparameters> hyperparameters,
    Optional<String> suffix,
    Optional<String> validationFile,
    Optional<List<Integration>> integrations,
    Optional<Integer> seed) {

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

  public sealed interface Integration permits Integration.Wandb {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String type();

    record Wandb(
        String project,
        Optional<String> name,
        Optional<String> entity,
        Optional<Map<String, String>> tags)
        implements Integration {

      @Override
      public String type() {
        return Constants.WANDB_INTEGRATION_TYPE;
      }

      public static Builder newBuilder() {
        return new Builder();
      }

      public static class Builder {

        private String project;
        private Optional<String> name = Optional.empty();
        private Optional<String> entity = Optional.empty();
        private Optional<Map<String, String>> tags = Optional.empty();

        /**
         * @param project The name of the project that the new run will be created under.
         */
        public Builder project(String project) {
          this.project = project;
          return this;
        }

        /**
         * @param name A display name to set for the run. If not set, we will use the Job ID as the
         *     name.
         */
        public Builder name(String name) {
          this.name = Optional.of(name);
          return this;
        }

        /**
         * @param entity The entity to use for the run. This allows you to set the team or username
         *     of the WandB user that you would like associated with the run. If not set, the
         *     default entity for the registered WandB API key is used.
         */
        public Builder entity(String entity) {
          this.entity = Optional.of(entity);
          return this;
        }

        /**
         * @param project A list of tags to be attached to the newly created run. These tags are
         *     passed through directly to WandB. Some default tags are generated by OpenAI:
         *     "openai/finetune", "openai/{base-model}", "openai/{ftjob-abcdef}".
         */
        public Builder tags(Map<String, String> tags) {
          this.tags = Optional.of(tags);
          return this;
        }

        public Wandb build() {
          return new Wandb(project, name, entity, tags);
        }
      }
    }
  }

  public static class Builder {

    private String model;
    private String trainingFile;
    private Optional<Hyperparameters> hyperparameters = Optional.empty();
    private Optional<String> suffix = Optional.empty();
    private Optional<String> validationFile = Optional.empty();
    private Optional<List<Integration>> integrations = Optional.empty();
    private Optional<Integer> seed = Optional.empty();

    /**
     * @param model The name of the model to fine-tune
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param model {@link OpenAIModel} to fine-tune
     */
    public Builder model(OpenAIModel model) {
      this.model = model.getId();
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

    /**
     * @param integrations A list of integrations to enable for your fine-tuning job.
     */
    public Builder integrations(List<Integration> integrations) {
      this.integrations = Optional.of(integrations);
      return this;
    }

    /**
     * @param seed The seed controls the reproducibility of the job. Passing in the same seed and
     *     job parameters should produce the same results, but may differ in rare cases. If a seed
     *     is not specified, one will be generated for you.
     */
    public Builder seed(int seed) {
      this.seed = Optional.of(seed);
      return this;
    }

    public CreateFineTuningJobRequest build() {
      return new CreateFineTuningJobRequest(
          model, trainingFile, hyperparameters, suffix, validationFile, integrations, seed);
    }
  }
}
