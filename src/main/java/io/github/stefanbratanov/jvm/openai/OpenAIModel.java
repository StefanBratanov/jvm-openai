package io.github.stefanbratanov.jvm.openai;

/**
 * Represents the latest OpenAI models.
 *
 * <p>Note that this does not correspond to a static version and may change over time.
 *
 * <p>To see the compatibility of the models with different API endpoints, refer to <a
 * href="https://platform.openai.com/docs/models/model-endpoint-compatibility">Model endpoint
 * compatibility</a>.
 *
 * @see <a href="https://platform.openai.com/docs/models/continuous-model-upgrades">Continuous model
 *     upgrades - OpenAI API</a>
 */
public enum OpenAIModel {

  // GPT-4 and GPT-4 Turbo (https://platform.openai.com/docs/models/gpt-3-5-turbo)
  GPT_4("gpt-4"),
  GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"),
  GPT_4_VISION_PREVIEW("gpt-4-vision-preview"),
  GPT_4_32K("gpt-4-32k"),

  // GPT-3.5 Turbo (https://platform.openai.com/docs/models/gpt-3-5-turbo)
  GPT_3_5_TURBO("gpt-3.5-turbo"),

  // DALL·E (https://platform.openai.com/docs/models/dall-e)
  DALL_E_3("dall-e-3"),
  DALL_E_2("dall-e-2"),

  // TTS (https://platform.openai.com/docs/models/tts)
  TTS_1("tts-1"),
  TTS_1_HD("tts-1-hd"),

  // whisper (https://platform.openai.com/docs/models/whisper)
  WHISPER_1("whisper-1"),

  // Embeddings (https://platform.openai.com/docs/models/embeddings)
  TEXT_EMBEDDING_3_LARGE("text-embedding-3-large"),
  TEXT_EMBEDDING_3_SMALL("text-embedding-3-small"),
  TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),

  // Moderation (https://platform.openai.com/docs/models/moderation)
  TEXT_MODERATION_LATEST("text-moderation-latest"),
  TEXT_MODERATION_STABLE("text-moderation-stable");

  private final String id;

  OpenAIModel(String modelId) {
    this.id = modelId;
  }

  public String getId() {
    return this.id;
  }
}
