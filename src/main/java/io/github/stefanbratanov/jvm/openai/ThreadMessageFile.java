package io.github.stefanbratanov.jvm.openai;

/** A list of files attached to a message. */
public record ThreadMessageFile(String id, long createdAt, String messageId) {}
