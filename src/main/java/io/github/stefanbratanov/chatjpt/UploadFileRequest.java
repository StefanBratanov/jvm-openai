package io.github.stefanbratanov.chatjpt;

import java.nio.file.Path;

public record UploadFileRequest(Path file, String purpose) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Path file;
    private String purpose;

    /**
     * @param file The File object (not file name) to be uploaded.
     */
    public Builder file(Path file) {
      this.file = file;
      return this;
    }

    /**
     * @param purpose The intended purpose of the uploaded file.
     */
    public Builder purpose(String purpose) {
      this.purpose = purpose;
      return this;
    }

    public UploadFileRequest build() {
      if (file == null) {
        throw new IllegalStateException("file must be set");
      }
      if (purpose == null) {
        throw new IllegalStateException("purpose must be set");
      }
      return new UploadFileRequest(file, purpose);
    }
  }
}
