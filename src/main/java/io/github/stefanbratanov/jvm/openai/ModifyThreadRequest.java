package io.github.stefanbratanov.jvm.openai;

import java.util.Map;
import java.util.Optional;

public record ModifyThreadRequest(
    Optional<ToolResources> toolResources, Optional<Map<String, String>> metadata) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Optional<ToolResources> toolResources = Optional.empty();
    private Optional<Map<String, String>> metadata = Optional.empty();

    /**
     * @param toolResources A set of resources that are made available to the assistant's tools in
     *     this thread. The resources are specific to the type of tool. For example, the
     *     code_interpreter tool requires a list of file IDs, while the file_search tool requires a
     *     list of vector store IDs.
     */
    public Builder toolResources(ToolResources toolResources) {
      this.toolResources = Optional.of(toolResources);
      return this;
    }

    /**
     * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be
     *     useful for storing additional information about the object in a structured format. Keys
     *     can be a maximum of 64 characters long and values can be a maxium of 512 characters long.
     */
    public Builder metadata(Map<String, String> metadata) {
      this.metadata = Optional.of(metadata);
      return this;
    }

    public ModifyThreadRequest build() {
      return new ModifyThreadRequest(toolResources, metadata);
    }
  }
}
