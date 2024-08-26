package io.github.stefanbratanov.jvm.openai;

/** Represents an individual user in a project. */
public record ProjectUser(String id, String name, String email, String role, long addedAt) {}
