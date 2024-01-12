package io.github.stefanbratanov.jvm.openai;

/**
 * Exceptions will be based on <a
 * href="https://platform.openai.com/docs/guides/error-codes/api-errors">Error Codes - OpenAI
 * API</a>
 */
public final class OpenAIException extends RuntimeException {

  private final int statusCode;
  private final Error error;

  OpenAIException(int statusCode, Error error) {
    super(String.format("%d - %s", statusCode, error));
    this.statusCode = statusCode;
    this.error = error;
  }

  public record Error(String message, String type, String param, String code) {
    @Override
    public String toString() {
      return String.format(
          "message: %s, type: %s, param: %s, code: %s", message, type, param, code);
    }
  }

  public int statusCode() {
    return statusCode;
  }

  public String errorMessage() {
    return error.message();
  }

  public Error error() {
    return error;
  }
}
