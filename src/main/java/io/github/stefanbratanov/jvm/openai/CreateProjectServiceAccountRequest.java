package io.github.stefanbratanov.jvm.openai;

public record CreateProjectServiceAccountRequest(String name) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;

    /**
     * @param name The name of the service account being created.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public CreateProjectServiceAccountRequest build() {
      return new CreateProjectServiceAccountRequest(name);
    }
  }
}
