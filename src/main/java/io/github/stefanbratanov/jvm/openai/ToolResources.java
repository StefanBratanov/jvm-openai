package io.github.stefanbratanov.jvm.openai;

import io.github.stefanbratanov.jvm.openai.ToolResources.FileSearch.VectorStores;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** A set of resources that are used by the assistant's tools. */
public record ToolResources(CodeInterpreter codeInterpreter, FileSearch fileSearch) {

  record CodeInterpreter(List<String> fileIds) {}

  /**
   * Either {@link #vectorStoreIds} or {@link #vectorStores} must be present, but not both of them.
   */
  record FileSearch(Optional<String[]> vectorStoreIds, Optional<VectorStores[]> vectorStores) {

    public record VectorStores(List<String> fileIds, Map<String, String> metadata) {}
  }

  static ToolResources codeInterpreterToolResources(List<String> fileIds) {
    return new ToolResources(new CodeInterpreter(fileIds), null);
  }

  static ToolResources fileSearchToolResources(String... vectorStoreIds) {
    return new ToolResources(null, new FileSearch(Optional.of(vectorStoreIds), Optional.empty()));
  }

  static ToolResources fileSearchToolResources(VectorStores... vectorStores) {
    return new ToolResources(null, new FileSearch(Optional.empty(), Optional.of(vectorStores)));
  }

  static ToolResources codeInterpreterAndFileSearchToolResources(
      List<String> fileIds, String... vectorStoreIds) {
    return new ToolResources(
        new CodeInterpreter(fileIds),
        new FileSearch(Optional.of(vectorStoreIds), Optional.empty()));
  }

  static ToolResources codeInterpreterAndFileSearchToolResources(
      List<String> fileIds, VectorStores... vectorStores) {
    return new ToolResources(
        new CodeInterpreter(fileIds), new FileSearch(Optional.empty(), Optional.of(vectorStores)));
  }
}
