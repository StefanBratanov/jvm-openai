package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Optional;

public record ListAuditLogsQueryParameters(
    Optional<EffectiveAt> effectiveAt,
    Optional<List<String>> projectIds,
    Optional<List<String>> eventTypes,
    Optional<List<String>> actorIds,
    Optional<List<String>> actorEmails,
    Optional<List<String>> resourceIds,
    Optional<String> after,
    Optional<String> before,
    Optional<Integer> limit) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public record EffectiveAt(
      Optional<Integer> gt, Optional<Integer> gte, Optional<Integer> lt, Optional<Integer> lte) {

    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {
      private Optional<Integer> gt = Optional.empty();
      private Optional<Integer> gte = Optional.empty();
      private Optional<Integer> lt = Optional.empty();
      private Optional<Integer> lte = Optional.empty();

      /**
       * @param gt Return only events whose effective_at (Unix seconds) is greater than this value.
       */
      public Builder gt(int gt) {
        this.gt = Optional.of(gt);
        return this;
      }

      /**
       * @param gte Return only events whose effective_at (Unix seconds) is greater than or equal to
       *     this value.
       */
      public Builder gte(int gte) {
        this.gte = Optional.of(gte);
        return this;
      }

      /**
       * @param lt Return only events whose effective_at (Unix seconds) is less than this value.
       */
      public Builder lt(int lt) {
        this.lt = Optional.of(lt);
        return this;
      }

      /**
       * @param lte Return only events whose effective_at (Unix seconds) is less than or equal to
       *     this value.
       */
      public Builder lte(int lte) {
        this.lte = Optional.of(lte);
        return this;
      }

      public EffectiveAt build() {
        return new EffectiveAt(gt, gte, lt, lte);
      }
    }
  }

  public static class Builder {

    private Optional<EffectiveAt> effectiveAt = Optional.empty();
    private Optional<List<String>> projectIds = Optional.empty();
    private Optional<List<String>> eventTypes = Optional.empty();
    private Optional<List<String>> actorIds = Optional.empty();
    private Optional<List<String>> actorEmails = Optional.empty();
    private Optional<List<String>> resourceIds = Optional.empty();
    private Optional<String> after = Optional.empty();
    private Optional<String> before = Optional.empty();
    private Optional<Integer> limit = Optional.empty();

    /**
     * @param effectiveAt Return only events whose effective_at (Unix seconds) is in this range.
     */
    public Builder effectiveAt(EffectiveAt effectiveAt) {
      this.effectiveAt = Optional.of(effectiveAt);
      return this;
    }

    /**
     * @param projectIds Return only events for these projects.
     */
    public Builder projectIds(List<String> projectIds) {
      this.projectIds = Optional.of(projectIds);
      return this;
    }

    /**
     * @param eventTypes Return only events with a type in one of these values.
     */
    public Builder eventTypes(List<String> eventTypes) {
      this.eventTypes = Optional.of(eventTypes);
      return this;
    }

    /**
     * @param actorIds Return only events performed by these actors. Can be a user ID, a service
     *     account ID, or an api key tracking ID.
     */
    public Builder actorIds(List<String> actorIds) {
      this.actorIds = Optional.of(actorIds);
      return this;
    }

    /**
     * @param actorEmails Return only events performed by users with these emails.
     */
    public Builder actorEmails(List<String> actorEmails) {
      this.actorEmails = Optional.of(actorEmails);
      return this;
    }

    /**
     * @param resourceIds Return only events performed on these targets. For example, a project ID
     *     updated.
     */
    public Builder resourceIds(List<String> resourceIds) {
      this.resourceIds = Optional.of(resourceIds);
      return this;
    }

    /**
     * @param after A cursor for use in pagination. after is an object ID that defines your place in
     *     the list.
     */
    public Builder after(String after) {
      this.after = Optional.of(after);
      return this;
    }

    /**
     * @param before A cursor for use in pagination. before is an object ID that defines your place
     *     in the list.
     */
    public Builder before(String before) {
      this.before = Optional.of(before);
      return this;
    }

    /**
     * @param limit A limit on the number of objects to be returned.
     */
    public Builder limit(int limit) {
      this.limit = Optional.of(limit);
      return this;
    }

    public ListAuditLogsQueryParameters build() {
      return new ListAuditLogsQueryParameters(
          effectiveAt,
          projectIds,
          eventTypes,
          actorIds,
          actorEmails,
          resourceIds,
          after,
          before,
          limit);
    }
  }
}
