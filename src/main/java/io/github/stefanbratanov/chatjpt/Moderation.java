package io.github.stefanbratanov.chatjpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents policy compliance report by OpenAI's content moderation model against a given input.
 */
public record Moderation(String id, String model, List<Result> results) {

  public record Result(boolean flagged, Categories categories, CategoryScores categoryScores) {

    public record Categories(
        boolean hate,
        @JsonProperty("hate/threatening") boolean hateThreatening,
        boolean harassment,
        @JsonProperty("harassment/threatening") boolean harassmentThreatening,
        @JsonProperty("self-harm") boolean selfHarm,
        @JsonProperty("self-harm/intent") boolean selfHarmIntent,
        @JsonProperty("self-harm/instructions") boolean selfHarmInstructions,
        boolean sexual,
        @JsonProperty("sexual/minors") boolean sexualMinors,
        boolean violence,
        @JsonProperty("violence/graphic") boolean violenceGraphic) {}

    public record CategoryScores(
        Double hate,
        @JsonProperty("hate/threatening") Double hateThreatening,
        Double harassment,
        @JsonProperty("harassment/threatening") Double harassmentThreatening,
        @JsonProperty("self-harm") Double selfHarm,
        @JsonProperty("self-harm/intent") Double selfHarmIntent,
        @JsonProperty("self-harm/instructions") Double selfHarmInstructions,
        Double sexual,
        @JsonProperty("sexual/minors") Double sexualMinors,
        Double violence,
        @JsonProperty("violence/graphic") Double violenceGraphic) {}
  }
}
