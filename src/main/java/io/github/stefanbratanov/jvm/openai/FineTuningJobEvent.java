package io.github.stefanbratanov.jvm.openai;

public record FineTuningJobEvent(String id, long createdAt, String level, String message) {}
