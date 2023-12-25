package com.stefanbratanov.chatjpt;

public record Usage(int promptTokens, int completionTokens, int totalTokens) {}
