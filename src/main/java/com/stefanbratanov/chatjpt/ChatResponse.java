package com.stefanbratanov.chatjpt;

public record ChatResponse(String id, long created, String model, Message message) {}
