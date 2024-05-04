package io.github.stefanbratanov.jvm.openai;

public record CreateVectorStoreFileRequest(String fileId) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private String fileId;

    /**
     * @param fileId a File ID that the vector store should use. Useful for tools like file_search
     *     that can access files.
     */
    public Builder fileId(String fileId) {
      this.fileId = fileId;
      return this;
    }

    public CreateVectorStoreFileRequest build() {
      return new CreateVectorStoreFileRequest(fileId);
    }
  }
}
