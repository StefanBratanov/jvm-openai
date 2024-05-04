package io.github.stefanbratanov.jvm.openai;

import java.util.List;

public record CreateVectorStoreFileBatchRequest(List<String> fileIds) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private List<String> fileIds;

    /**
     * @param fileIds A list of File IDs that the vector store should use. Useful for tools like
     *     file_search that can access files.
     */
    public Builder fileIds(List<String> fileIds) {
      this.fileIds = fileIds;
      return this;
    }

    public CreateVectorStoreFileBatchRequest build() {
      return new CreateVectorStoreFileBatchRequest(fileIds);
    }
  }
}
