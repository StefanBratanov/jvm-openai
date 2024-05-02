package io.github.stefanbratanov.jvm.openai;

import java.util.List;
import java.util.Map;

/** Represents an assistant that can call the model and use tools. */
public record Assistant(
    String id,
    long createdAt,
    String name,
    String description,
    String model,
    String instructions,
    List<Tool> tools,
    ToolResources toolResources,
    Map<String, String> metadata,
    Double temperature,
    Double topP,
    AssistantsResponseFormat responseFormat) {}
