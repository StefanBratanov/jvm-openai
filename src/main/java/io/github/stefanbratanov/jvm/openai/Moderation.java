package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represents if a given text input is potentially harmful. */
public record Moderation(String id, String model, List<Result> results) {

  public record Result(
      boolean flagged,
      Categories categories,
      CategoryScores categoryScores,
      CategoryAppliedInputTypes categoryAppliedInputTypes) {

    public record Categories(
        boolean hate,
        @JsonProperty("hate/threatening") boolean hateThreatening,
        boolean harassment,
        @JsonProperty("harassment/threatening") boolean harassmentThreatening,
        boolean illicit,
        @JsonProperty("illicit/violent") boolean illicitViolent,
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
        Double illicit,
        @JsonProperty("illicit/violent") Double illicitViolent,
        @JsonProperty("self-harm") Double selfHarm,
        @JsonProperty("self-harm/intent") Double selfHarmIntent,
        @JsonProperty("self-harm/instructions") Double selfHarmInstructions,
        Double sexual,
        @JsonProperty("sexual/minors") Double sexualMinors,
        Double violence,
        @JsonProperty("violence/graphic") Double violenceGraphic) {}

    public record CategoryAppliedInputTypes(
        List<String> hate,
        @JsonProperty("hate/threatening") List<String> hateThreatening,
        List<String> harassment,
        @JsonProperty("harassment/threatening") List<String> harassmentThreatening,
        List<String> illicit,
        @JsonProperty("illicit/violent") List<String> illicitViolent,
        @JsonProperty("self-harm") List<String> selfHarm,
        @JsonProperty("self-harm/intent") List<String> selfHarmIntent,
        @JsonProperty("self-harm/instructions") List<String> selfHarmInstructions,
        List<String> sexual,
        @JsonProperty("sexual/minors") List<String> sexualMinors,
        List<String> violence,
        @JsonProperty("violence/graphic") List<String> violenceGraphic) {}
  }
}
