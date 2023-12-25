package io.github.stefanbratanov.chatjpt;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record ChatRequest(
    String model,
    List<Message> messages,
    Optional<Double> frequencyPenalty,
    Optional<Integer> maxTokens,
    Optional<Integer> n,
    Optional<Double> presencePenalty,
    Optional<Integer> seed,
    Optional<Double> temperature,
    Optional<Double> topP,
    Optional<String> user) {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    private String model = DEFAULT_MODEL;

    private final List<Message> messages = new LinkedList<>();

    private Optional<Double> frequencyPenalty = Optional.empty();
    private Optional<Integer> maxTokens = Optional.empty();
    private Optional<Integer> n = Optional.empty();
    private Optional<Double> presencePenalty = Optional.empty();
    private Optional<Integer> seed = Optional.empty();
    private Optional<Double> temperature = Optional.empty();
    private Optional<Double> topP = Optional.empty();
    private Optional<String> user = Optional.empty();

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

    /**
     * @param frequencyPenalty Number between -2.0 and 2.0. Positive values penalize new tokens
     *     based on their existing frequency in the text so far, decreasing the model's likelihood
     *     to repeat the same line verbatim.
     */
    public Builder frequencyPenalty(double frequencyPenalty) {
      if (frequencyPenalty < -2 || frequencyPenalty > 2) {
        throw new IllegalArgumentException(
            "frequencyPenalty should be between -2.0 and 2.0 but it was " + frequencyPenalty);
      }
      this.frequencyPenalty = Optional.of(frequencyPenalty);
      return this;
    }

    /**
     * @param maxTokens The total length of input tokens and generated tokens is limited by the
     *     model's context length
     */
    public Builder maxTokens(int maxTokens) {
      if (maxTokens < 1) {
        throw new IllegalArgumentException("maxTokens should be a positive number");
      }
      this.maxTokens = Optional.of(maxTokens);
      return this;
    }

    /**
     * @param n How many chat completion choices to generate for each input message. Note that you
     *     will be charged based on the number of generated tokens across all of the choices. Keep n
     *     as 1 to minimize costs.
     */
    public Builder n(int n) {
      if (n < 1) {
        throw new IllegalArgumentException("n should be a positive number");
      }
      this.n = Optional.of(n);
      return this;
    }

    /**
     * @param presencePenalty Number between -2.0 and 2.0. Positive values penalize new tokens based
     *     on whether they appear in the text so far, increasing the model's likelihood to talk
     *     about new topics.
     */
    public Builder presencePenalty(double presencePenalty) {
      if (presencePenalty < -2 || presencePenalty > 2) {
        throw new IllegalArgumentException(
            "presencePenalty should be between -2.0 and 2.0 but it was " + presencePenalty);
      }
      this.presencePenalty = Optional.of(presencePenalty);
      return this;
    }

    /**
     * @param seed If specified, the system will make a best effort to sample deterministically,
     *     such that repeated requests with the same seed and parameters should return the same
     *     result. Determinism is not guaranteed, and you should refer to the system_fingerprint
     *     response parameter to monitor changes in the backend.
     */
    public Builder seed(int seed) {
      this.seed = Optional.of(seed);
      return this;
    }

    /**
     * @param temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8
     *     will make the output more random, while lower values like 0.2 will make it more focused
     *     and deterministic.
     */
    public Builder temperature(double temperature) {
      if (temperature < 0 || temperature > 2) {
        throw new IllegalArgumentException(
            "temperature should be between 0 and 2 but it was " + temperature);
      }
      this.temperature = Optional.of(temperature);
      return this;
    }

    /**
     * @param topP An alternative to sampling with temperature, called nucleus sampling, where the
     *     model considers the results of the tokens with top_p probability mass. So 0.1 means only
     *     the tokens comprising the top 10% probability mass are considered.
     */
    public Builder topP(double topP) {
      this.topP = Optional.of(topP);
      return this;
    }

    /**
     * @param user A unique identifier representing your end-user, which can help OpenAI to monitor
     *     and detect abuse.
     */
    public Builder user(String user) {
      this.user = Optional.of(user);
      return this;
    }

    public ChatRequest build() {
      return new ChatRequest(
          model,
          List.copyOf(messages),
          frequencyPenalty,
          maxTokens,
          n,
          presencePenalty,
          seed,
          temperature,
          topP,
          user);
    }
  }
}
