package io.github.stefanbratanov.jvm.openai;

public record ModifyProjectUserRequest(String role) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String role;

    /**
     * @param role `owner` or `member`
     */
    public Builder role(String role) {
      this.role = role;
      return this;
    }

    public ModifyProjectUserRequest build() {
      return new ModifyProjectUserRequest(role);
    }
  }
}
