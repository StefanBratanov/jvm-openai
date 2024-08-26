package io.github.stefanbratanov.jvm.openai;

public record CreateProjectUserRequest(String userId, String role) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String userId;
    private String role;

    /**
     * @param userId The ID of the user.
     */
    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * @param role `owner` or `member`.
     */
    public Builder role(String role) {
      this.role = role;
      return this;
    }

    public CreateProjectUserRequest build() {
      return new CreateProjectUserRequest(userId, role);
    }
  }
}
