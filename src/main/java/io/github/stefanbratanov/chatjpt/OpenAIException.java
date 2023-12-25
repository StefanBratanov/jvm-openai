package io.github.stefanbratanov.chatjpt;

/**
 * Exceptions will be based on <a
 * href="https://platform.openai.com/docs/guides/error-codes/api-errors">Error Codes - OpenAI
 * API</a>
 */
public final class OpenAIException extends RuntimeException {

  private final int statusCode;
  private final String errorMessage;

  OpenAIException(int statusCode, String errorMessage) {
    super(String.format("%d - %s", statusCode, errorMessage));
    this.statusCode = statusCode;
    this.errorMessage = errorMessage;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
