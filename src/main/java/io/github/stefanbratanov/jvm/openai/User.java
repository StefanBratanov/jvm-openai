package io.github.stefanbratanov.jvm.openai;

/** Represents an individual user within an organization. */
public record User(String id, String name, String email, String role, long addedAt) {}
