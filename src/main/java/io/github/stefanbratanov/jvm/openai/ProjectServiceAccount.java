package io.github.stefanbratanov.jvm.openai;

/** Represents an individual service account in a project. */
public record ProjectServiceAccount(String id, String name, String role, long createdAt) {}
