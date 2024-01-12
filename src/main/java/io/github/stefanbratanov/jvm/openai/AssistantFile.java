package io.github.stefanbratanov.jvm.openai;

/** A list of Files attached to an assistant. */
public record AssistantFile(String id, long createdAt, String assistantId) {}
