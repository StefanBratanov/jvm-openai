package io.github.stefanbratanov.chatjpt;

/** A list of files attached to a message. */
public record ThreadMessageFile(String id, long createdAt, String messageId) {}
