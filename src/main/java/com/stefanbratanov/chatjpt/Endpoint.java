package com.stefanbratanov.chatjpt;

enum Endpoint {
  CHAT("chat/completions");

  private final String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }
}
