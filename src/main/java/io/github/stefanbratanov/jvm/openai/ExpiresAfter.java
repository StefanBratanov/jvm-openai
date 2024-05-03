package io.github.stefanbratanov.jvm.openai;

/**
 * The expiration policy for a vector store.
 *
 * @param anchor Anchor timestamp after which the expiration policy applies.
 * @param days The number of days after the anchor time that the vector store will expire.
 */
public record ExpiresAfter(String anchor, int days) {

  public static ExpiresAfter lastActiveAt(int days) {
    return new ExpiresAfter("last_active_at", days);
  }
}
