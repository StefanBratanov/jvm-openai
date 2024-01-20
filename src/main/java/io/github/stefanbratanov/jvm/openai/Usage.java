package io.github.stefanbratanov.jvm.openai;

/** Usage statistics */
public record Usage(int completionTokens, int promptTokens, int totalTokens) {}
