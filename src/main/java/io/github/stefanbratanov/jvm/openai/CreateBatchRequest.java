package io.github.stefanbratanov.jvm.openai;

import java.util.Map;
import java.util.Optional;

public record CreateBatchRequest(
    String inputFileId,
    String endpoint,
    String completionWindow,
    Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String inputFileId;
    private String endpoint;
    private String completionWindow;
    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param inputFileId The ID of an uploaded file that contains requests for the new batch. Your
     *     input file must be formatted as a JSONL file, and must be uploaded with the purpose
     *     batch.
     */
    public Builder inputFileId(String inputFileId) {
      this.inputFileId = inputFileId;
      return this;
    }

    /**
     * @param endpoint The endpoint to be used for all requests in the batch.
     */
    public Builder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    /**
     * @param completionWindow The time frame within which the batch should be processed.
     */
    public Builder completionWindow(String completionWindow) {
      this.completionWindow = completionWindow;
      return this;
    }

    /**
     * @param metadata Custom metadata for the batch.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    public CreateBatchRequest build() {
      return new CreateBatchRequest(inputFileId, endpoint, completionWindow, metadata);
    }
  }
}
