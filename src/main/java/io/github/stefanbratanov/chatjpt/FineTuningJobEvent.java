package io.github.stefanbratanov.chatjpt;

public record FineTuningJobEvent(String id, long createdAt, String level, String message) {}
