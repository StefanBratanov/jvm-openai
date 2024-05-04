package io.github.stefanbratanov.jvm.openai;

/** The last error associated with an object */
public record LastError(String code, String message) {}
