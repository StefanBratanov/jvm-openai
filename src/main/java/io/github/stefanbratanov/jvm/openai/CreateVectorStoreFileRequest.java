package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record CreateVectorStoreFileRequest(
    String fileId, Optional<ChunkingStrategy> chunkingStrategy) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String fileId;
    private Optional<ChunkingStrategy> chunkingStrategy = Optional.empty();

    /**
     * @param fileId a File ID that the vector store should use. Useful for tools like file_search
     *     that can access files.
     */
    public Builder fileId(String fileId) {
      this.fileId = fileId;
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

    public CreateVectorStoreFileRequest build() {
      return new CreateVectorStoreFileRequest(fileId, chunkingStrategy);
    }
  }
}
