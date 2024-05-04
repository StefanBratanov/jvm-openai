package io.github.stefanbratanov.jvm.openai;

import java.util.Optional;

public record PaginationQueryParameters(
    Optional<Integer> limit,
    Optional<String> order,
    Optional<String> before,
    Optional<String> after) {

  public static PaginationQueryParameters none() {
    return new Builder().build();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Optional<Integer> limit = Optional.empty();
    private Optional<String> order = Optional.empty();
    private Optional<String> before = Optional.empty();
    private Optional<String> after = Optional.empty();

    /**
     * @param limit A limit on the number of objects to be returned. Limit can range between 1 and
     *     100, and the default is 20.
     */
    public Builder limit(int limit) {
      this.limit = Optional.of(limit);
      return this;
    }

    /**
     * @param order Sort order by the created_at timestamp of the objects. asc for ascending order
     *     and desc for descending order.
     */
    public Builder order(String order) {
      this.order = Optional.of(order);
      return this;
    }

    /**
     * @param before A cursor for use in pagination. before is an object ID that defines your place
     *     in the list. For instance, if you make a list request and receive 100 objects, ending
     *     with obj_foo, your subsequent call can include before=obj_foo in order to fetch the
     *     previous page of the list.
     */
    public Builder before(String before) {
      this.before = Optional.of(before);
      return this;
    }

    /**
     * @param after A cursor for use in pagination. after is an object ID that defines your place in
     *     the list. For instance, if you make a list request and receive 100 objects, ending with
     *     obj_foo, your subsequent call can include after=obj_foo in order to fetch the next page
     *     of the list.
     */
    public Builder after(String after) {
      this.after = Optional.of(after);
      return this;
    }

    public PaginationQueryParameters build() {
      return new PaginationQueryParameters(limit, order, before, after);
    }
  }
}
