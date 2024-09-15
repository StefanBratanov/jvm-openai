package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record CreateProjectRequest(
    String name, Optional<String> appUseCase, Optional<String> businessWebsite) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String name;

    private Optional<String> appUseCase = Optional.empty();
    private Optional<String> businessWebsite = Optional.empty();

    /**
     * @param name The friendly name of the project, this name appears in reports.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * @param appUseCase A description of your business, project, or use case.
     */
    public Builder appUseCase(String appUseCase) {
      this.appUseCase = Optional.of(appUseCase);
      return this;
    }

    /**
     * @param businessWebsite Your business URL, or if you don't have one yet, a URL to your
     *     LinkedIn or other social media.
     */
    public Builder businessWebsite(String businessWebsite) {
      this.businessWebsite = Optional.of(businessWebsite);
      return this;
    }

    public CreateProjectRequest build() {
      return new CreateProjectRequest(name, appUseCase, businessWebsite);
    }
  }
}
