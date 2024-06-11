package io.github.stefanbratanov.jvm.openai;

import io.github.stefanbratanov.jvm.openai.ToolResources.FileSearch.VectorStore;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** A set of resources that are used by the assistant's tools. */
public record ToolResources(CodeInterpreter codeInterpreter, FileSearch fileSearch) {

  public record CodeInterpreter(List<String> fileIds) {}

  /**
   * Either {@link #vectorStoreIds} or {@link #vectorStores} must be present, but not both of them.
   */
  public record FileSearch(
      Optional<String[]> vectorStoreIds, Optional<VectorStore[]> vectorStores) {

    public record VectorStore(
        Optional<List<String>> fileIds,
        Optional<ChunkingStrategy> chunkingStrategy,
        Optional<Map<String, String>> metadata) {

      public static Builder newBuilder() {
        return new Builder();
      }

      public static class Builder {
        private Optional<List<String>> fileIds = Optional.empty();
        private Optional<ChunkingStrategy> chunkingStrategy = Optional.empty();
        private Optional<Map<String, String>> metadata = Optional.empty();

        /**
         * @param fileIds A list of file IDs to add to the vector store.
         */
        public Builder fileIds(List<String> fileIds) {
          this.fileIds = Optional.of(fileIds);
          return this;
        }

        /**
         * @param chunkingStrategy The chunking strategy used to chunk the file(s)
         */
        public Builder chunkingStrategy(ChunkingStrategy chunkingStrategy) {
          this.chunkingStrategy = Optional.of(chunkingStrategy);
          return this;
        }

        /**
         * @param metadata Set of 16 key-value pairs that can be attached to a vector store. This
         *     can be useful for storing additional information about the vector store in a
         *     structured format.
         */
        public Builder metadata(Map<String, String> metadata) {
          this.metadata = Optional.of(metadata);
          return this;
        }

        public VectorStore build() {
          return new VectorStore(fileIds, chunkingStrategy, metadata);
        }
      }
    }
  }

  public static ToolResources codeInterpreterToolResources(List<String> fileIds) {
    return new ToolResources(new CodeInterpreter(fileIds), null);
  }

  public static ToolResources fileSearchToolResources(String... vectorStoreIds) {
    return new ToolResources(null, new FileSearch(Optional.of(vectorStoreIds), Optional.empty()));
  }

  public static ToolResources fileSearchToolResources(VectorStore... vectorStores) {
    return new ToolResources(null, new FileSearch(Optional.empty(), Optional.of(vectorStores)));
  }

  public static ToolResources codeInterpreterAndFileSearchToolResources(
      List<String> fileIds, String... vectorStoreIds) {
    return new ToolResources(
        new CodeInterpreter(fileIds),
        new FileSearch(Optional.of(vectorStoreIds), Optional.empty()));
  }

  public static ToolResources codeInterpreterAndFileSearchToolResources(
      List<String> fileIds, VectorStore... vectorStores) {
    return new ToolResources(
        new CodeInterpreter(fileIds), new FileSearch(Optional.empty(), Optional.of(vectorStores)));
  }
}
