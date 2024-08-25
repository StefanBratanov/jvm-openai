package io.github.stefanbratanov.jvm.openai;

public record InviteRequest(String email, String role) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String email;
    private String role;

    /**
     * @param email Send an email to this address
     */
    public Builder email(String email) {
      this.email = email;
      return this;
    }

    /**
     * @param role `owner` or `reader`
     */
    public Builder role(String role) {
      this.role = role;
      return this;
    }

    public InviteRequest build() {
      return new InviteRequest(email, role);
    }
  }
}
