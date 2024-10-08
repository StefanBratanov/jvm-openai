package io.github.stefanbratanov.jvm.openai;

public record CreateProjectRequest(String name) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;

    /**
     * @param name The friendly name of the project, this name appears in reports.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public CreateProjectRequest build() {
      return new CreateProjectRequest(name);
    }
  }
}
