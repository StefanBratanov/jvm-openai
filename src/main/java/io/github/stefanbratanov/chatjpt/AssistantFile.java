package io.github.stefanbratanov.chatjpt;

/** A list of Files attached to an assistant. */
public record AssistantFile(String id, long createdAt, String assistantId) {}
