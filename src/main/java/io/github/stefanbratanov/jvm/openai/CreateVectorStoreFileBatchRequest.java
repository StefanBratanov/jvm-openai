package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Optional;

public record CreateVectorStoreFileBatchRequest(
    List<String> fileIds, Optional<ChunkingStrategy> chunkingStrategy) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private List<String> fileIds;
    private Optional<ChunkingStrategy> chunkingStrategy;

    /**
     * @param fileIds A list of File IDs that the vector store should use. Useful for tools like
     *     file_search that can access files.
     */
    public Builder fileIds(List<String> fileIds) {
      this.fileIds = fileIds;
      return this;
    }

    /**
     * @param chunkingStrategy The chunking strategy used to chunk the file(s). If not set, will use
     *     the `auto` strategy.
     */
    public Builder chunkingStrategy(ChunkingStrategy chunkingStrategy) {
      this.chunkingStrategy = Optional.of(chunkingStrategy);
      return this;
    }

    public CreateVectorStoreFileBatchRequest build() {
      return new CreateVectorStoreFileBatchRequest(fileIds, chunkingStrategy);
    }
  }
}
