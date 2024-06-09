package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = ChunkingStrategy.AutoChunkingStrategy.class,
      name = Constants.AUTO_CHUNKING_STRATEGY),
  @JsonSubTypes.Type(
      value = ChunkingStrategy.StaticChunkingStrategy.class,
      name = Constants.STATIC_CHUNKING_STRATEGY),
  @JsonSubTypes.Type(
      value = ChunkingStrategy.OtherChunkingStrategy.class,
      name = Constants.OTHER_CHUNKING_STRATEGY)
})
public sealed interface ChunkingStrategy
    permits ChunkingStrategy.AutoChunkingStrategy,
        ChunkingStrategy.StaticChunkingStrategy,
        ChunkingStrategy.OtherChunkingStrategy {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String type();

  record AutoChunkingStrategy() implements ChunkingStrategy {

    @Override
    public String type() {
      return Constants.AUTO_CHUNKING_STRATEGY;
    }
  }

  record StaticChunkingStrategy(@JsonProperty("static") Static aStatic)
      implements ChunkingStrategy {

    public record Static(int maxChunkSizeTokens, int chunkOverlapTokens) {}

    @Override
    public String type() {
      return Constants.STATIC_CHUNKING_STRATEGY;
    }
  }

  record OtherChunkingStrategy() implements ChunkingStrategy {

    @Override
    public String type() {
      return Constants.OTHER_CHUNKING_STRATEGY;
    }
  }

  static AutoChunkingStrategy autoChunkingStrategy() {
    return new AutoChunkingStrategy();
  }

  /**
   * @param maxChunkSizeTokens The maximum number of tokens in each chunk.
   * @param chunkOverlapTokens The number of tokens that overlap between chunks. Note that the
   *     overlap must not exceed half of `max_chunk_size_tokens`.
   */
  static StaticChunkingStrategy staticChunkingStrategy(
      int maxChunkSizeTokens, int chunkOverlapTokens) {
    return new StaticChunkingStrategy(
        new StaticChunkingStrategy.Static(maxChunkSizeTokens, chunkOverlapTokens));
  }

  static OtherChunkingStrategy otherChunkingStrategy() {
    return new OtherChunkingStrategy();
  }
}
