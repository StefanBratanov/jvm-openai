package io.github.stefanbratanov.jvm.openai;

/** Represents an individual project. */
public record Project(String id, String name, long createdAt, Long archivedAt, String status) {}
