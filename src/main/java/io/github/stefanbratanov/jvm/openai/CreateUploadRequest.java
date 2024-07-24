package io.github.stefanbratanov.jvm.openai;

public record CreateUploadRequest(String filename, String purpose, int bytes, String mimeType) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String filename;
    private String purpose;
    private int bytes;
    private String mimeType;

    /**
     * @param filename The name of the file to upload.
     */
    public Builder filename(String filename) {
      this.filename = filename;
      return this;
    }

    /**
     * @param purpose The intended purpose of the uploaded file.
     */
    public Builder purpose(String purpose) {
      this.purpose = purpose;
      return this;
    }

    /**
     * @param purpose The intended purpose of the uploaded file.
     */
    public Builder purpose(Purpose purpose) {
      this.purpose = purpose.getId();
      return this;
    }

    /**
     * @param bytes The number of bytes in the file you are uploading.
     */
    public Builder bytes(int bytes) {
      this.bytes = bytes;
      return this;
    }

    /**
     * @param mimeType The MIME type of the file.
     *     <p>This must fall within the supported MIME types for your file purpose. See the
     *     supported MIME types for assistants and vision.
     */
    public Builder mimeType(String mimeType) {
      this.mimeType = mimeType;
      return this;
    }

    public CreateUploadRequest build() {
      return new CreateUploadRequest(filename, purpose, bytes, mimeType);
    }
  }
}
