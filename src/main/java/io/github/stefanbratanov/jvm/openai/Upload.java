package io.github.stefanbratanov.jvm.openai;

public record Upload(
    String id,
    int createdAt,
    String filename,
    int bytes,
    String purpose,
    String status,
    int expiresAt,
    File file) {}
