package com.stefanbratanov.chatjpt;

import java.util.Optional;

class Utils {

  static String[] getAuthenticationHeaders(String apiKey, Optional<String> organization) {
    return organization
        .map(org -> new String[] {"Authorization", "Bearer " + apiKey, "OpenAI-Organization", org})
        .orElse(new String[] {"Authorization", "Bearer " + apiKey});
  }
}
