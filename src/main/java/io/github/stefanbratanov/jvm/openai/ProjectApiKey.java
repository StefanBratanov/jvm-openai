package io.github.stefanbratanov.jvm.openai;

/** Represents an individual API key in a project. */
public record ProjectApiKey(
    String redactedValue, String name, long createdAt, String id, Owner owner) {

  public record Owner(String type, ProjectUser user, ProjectServiceAccount serviceAccount) {}
}
