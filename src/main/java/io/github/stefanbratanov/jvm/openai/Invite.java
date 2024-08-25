package io.github.stefanbratanov.jvm.openai;

/** Represents an individual `invite` to the organization. */
public record Invite(
    String id,
    String email,
    String role,
    String status,
    long invitedAt,
    long expiresAt,
    Long acceptedAt) {}
