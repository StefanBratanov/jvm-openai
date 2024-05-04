package io.github.stefanbratanov.jvm.openai;

/** A batch of files attached to a vector store. */
public record VectorStoreFileBatch(
    String id, long createdAt, String vectorStoreId, String status, FileCounts fileCounts) {

  public record FileCounts(int inProgress, int completed, int failed, int cancelled, int total) {}
}
