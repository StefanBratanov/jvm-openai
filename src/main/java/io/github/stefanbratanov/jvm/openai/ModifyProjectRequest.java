package io.github.stefanbratanov.jvm.openai;

public record ModifyProjectRequest(String name) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;

    /**
     * @param name The updated name of the project, this name appears in reports.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public ModifyProjectRequest build() {
      return new ModifyProjectRequest(name);
    }
  }
}
