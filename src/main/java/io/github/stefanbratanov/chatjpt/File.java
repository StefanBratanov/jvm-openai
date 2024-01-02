package io.github.stefanbratanov.chatjpt;

public record File(String id, int bytes, long createdAt, String filename, String purpose) {}
