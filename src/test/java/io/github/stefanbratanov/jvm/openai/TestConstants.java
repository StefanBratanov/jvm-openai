package io.github.stefanbratanov.jvm.openai;

public class TestConstants {

  private TestConstants() {}

  // change after https://github.com/openai/openai-openapi/pull/313 and
  // https://github.com/openai/openai-openapi/pull/314 are merged
  public static final String OPEN_AI_SPECIFICATION_URL =
      "https://raw.githubusercontent.com/StefanBratanov/openai-openapi/temp_fixes/openapi.yaml";
}
