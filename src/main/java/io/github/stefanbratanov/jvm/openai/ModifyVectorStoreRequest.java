package io.github.stefanbratanov.jvm.openai;

import java.util.Map;
import java.util.Optional;

public record ModifyVectorStoreRequest(
    Optional<String> name,
    Optional<ExpiresAfter> expiresAfter,
    Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Optional<String> name = Optional.empty();
    private Optional<ExpiresAfter> expiresAfter = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param name The name of the vector store.
     */
    public Builder name(String name) {
      this.name = Optional.of(name);
      return this;
    }

    /**
     * @param expiresAfter The expiration policy for a vector store.
     */
    public Builder expiresAfter(ExpiresAfter expiresAfter) {
      this.expiresAfter = Optional.of(expiresAfter);
      return this;
    }

    /**
     * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be
     *     useful for storing additional information about the object in a structured format. Keys
     *     can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    public ModifyVectorStoreRequest build() {
      return new ModifyVectorStoreRequest(name, expiresAfter, metadata);
    }
  }
}
