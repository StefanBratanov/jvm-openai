package com.stefanbratanov.chatjpt;

import java.util.LinkedList;
import java.util.List;

public record ChatRequest(String model, List<Message> messages) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    private String model = DEFAULT_MODEL;

    private final List<Message> messages = new LinkedList<>();

    /**
     * @param model ID of the model to use
     */
    public Builder model(String model) {
      this.model = model;
      return this;
    }

    /**
     * @param message message to append to the list of messages comprising the conversation so far
     */
    public Builder message(Message message) {
      messages.add(message);
      return this;
    }

    /**
     * @param messages messages to append to the list of messages comprising the conversation so far
     */
    public Builder messages(List<Message> messages) {
      this.messages.addAll(messages);
      return this;
    }

    public ChatRequest build() {
      return new ChatRequest(model, List.copyOf(messages));
    }
  }
}
