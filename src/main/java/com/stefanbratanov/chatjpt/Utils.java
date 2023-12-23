package com.stefanbratanov.chatjpt;

class Utils {

  static String[] getAuthorizationHeader(String apiKey) {
    return new String[] {"Authorization", "Bearer " + apiKey};
  }
}
