package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.stefanbratanov.jvm.openai.CompletionModel.ContinuousCompletionModel;
import io.github.stefanbratanov.jvm.openai.CompletionModel.CompletionModelId;

/**
 * Represents a completion model that can be used to generate completions.
 *
 * <p>To use latest models, use the {@link ContinuousCompletionModel} enum. To use static models,
 * use {@link #of(String modelId)}.
 */
public sealed interface CompletionModel permits ContinuousCompletionModel, CompletionModelId {

    static CompletionModel of(String id) {
        return CompletionModelId.of(id);
    }

    /**
     * Represents the latest models that can be used to generate completions.
     *
     * <p>Note that this does not correspond to a static version and may change over time.
     *
     * <p>Id you need a specific version, use {@link CompletionModelId#of(String modelId)} instead.
     *
     * @see <a href="https://platform.openai.com/docs/models/continuous-model-upgrades">Continuous model upgrades - OpenAI API</a>
     */
    enum ContinuousCompletionModel implements CompletionModel {
        GPT_4("gpt-4"),
        GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"),
        GPT_4_VISION_PREVIEW("gpt-4-vision-preview"),
        GPT_4_32K("gpt-4-32k"),
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k");

        private final String id;

        ContinuousCompletionModel(String modelId) {
            this.id = modelId;
        }

        @Override
        public String getId() {
            return this.id;
        }
    }

    /**
     * Represents a static completion model that can be used to generate completions.
     */
    final class CompletionModelId implements CompletionModel {

        private final String id;

        static CompletionModelId of(String id) {
            return new CompletionModelId(id);
        }

        private CompletionModelId(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return this.id;
        }
    }

    @JsonValue
    String getId();
}
