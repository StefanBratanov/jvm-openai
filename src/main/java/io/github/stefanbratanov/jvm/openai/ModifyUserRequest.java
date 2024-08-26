package io.github.stefanbratanov.jvm.openai;

public record ModifyUserRequest(String role) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String role;

    /**
     * @param role `owner` or `reader`
     */
    public Builder role(String role) {
      this.role = role;
      return this;
    }

    public ModifyUserRequest build() {
      return new ModifyUserRequest(role);
    }
  }
}
