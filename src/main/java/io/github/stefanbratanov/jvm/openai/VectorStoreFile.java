package io.github.stefanbratanov.jvm.openai;

/** A file attached to a vector store. */
public record VectorStoreFile(
    String id,
    long usageBytes,
    long createdAt,
    String vectorStoreId,
    String status,
    LastError lastError,
    ChunkingStrategy chunkingStrategy) {}
