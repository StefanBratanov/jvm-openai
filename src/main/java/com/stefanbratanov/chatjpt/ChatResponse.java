package com.stefanbratanov.chatjpt;

public record ChatResponse(
    String id,
    long created,
    String model,
    String systemFingerprint,
    Message message,
    Usage usage) {}
