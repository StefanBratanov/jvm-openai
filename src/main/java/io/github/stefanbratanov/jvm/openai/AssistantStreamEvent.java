package io.github.stefanbratanov.jvm.openai;

/**
 * @see <a
 *     href="https://platform.openai.com/docs/api-reference/assistants-streaming/events">Assistant
 *     stream events</a>
 * @param event the type of the emitted event
 * @param data the event data or null if the event type is not supported
 */
public record AssistantStreamEvent(String event, Data data) {

  public sealed interface Data
      permits Thread,
          ThreadRun,
          ThreadRunStep,
          ThreadRunStepDelta,
          ThreadMessage,
          ThreadMessageDelta {}
}
