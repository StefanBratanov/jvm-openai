package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Optional;

public record CompleteUploadRequest(List<String> partIds, Optional<String> md5) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private List<String> partIds;

    private Optional<String> md5 = Optional.empty();

    /**
     * @param partIds The ordered list of Part IDs.
     */
    public Builder partIds(List<String> partIds) {
      this.partIds = partIds;
      return this;
    }

    /**
     * @param md5 The optional md5 checksum for the file contents to verify if the bytes uploaded
     *     matches what you expect.
     */
    public Builder md5(String md5) {
      this.md5 = Optional.of(md5);
      return this;
    }

    public CompleteUploadRequest build() {
      return new CompleteUploadRequest(partIds, md5);
    }
  }
}
