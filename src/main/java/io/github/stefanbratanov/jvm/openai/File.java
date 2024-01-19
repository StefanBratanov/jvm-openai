package io.github.stefanbratanov.jvm.openai;

public record File(
    String id,
    int bytes,
    long createdAt,
    String filename,
    String purpose,
    @Deprecated(forRemoval = true) String status) {}
