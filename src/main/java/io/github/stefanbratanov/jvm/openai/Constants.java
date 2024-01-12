package io.github.stefanbratanov.jvm.openai;

class Constants {

  private Constants() {}

  static final String JSON_MEDIA_TYPE = "application/json";
  static final String CONTENT_TYPE_HEADER = "Content-Type";
  static final String ACCEPT_HEADER = "Accept";

  static final String SYSTEM_MESSAGE_ROLE = "system";
  static final String USER_MESSAGE_ROLE = "user";
  static final String ASSISTANT_MESSAGE_ROLE = "assistant";
  static final String TOOL_MESSAGE_ROLE = "tool";

  static final String IMAGE_FILE_MESSAGE_CONTENT_TYPE = "image_file";
  static final String TEXT_MESSAGE_CONTENT_TYPE = "text";

  static final String FILE_CITATION_ANNOTATION_TYPE = "file_citation";
  static final String FILE_PATH_ANNOTATION_TYPE = "file_path";

  static final String CODE_INTERPRETER_TOOL_TYPE = "code_interpreter";
  static final String RETRIEVAL_TOOL_TYPE = "retrieval";
  static final String FUNCTION_TOOL_TYPE = "function";

  static final String MESSAGE_CREATION_STEP_DETAILS_TYPE = "message_creation";
  static final String TOOL_CALLS_STEP_DETAILS_TYPE = "tool_calls";

  static final String CODE_INTERPRETER_TOOL_CALL_TYPE = "code_interpreter";
  static final String RETRIEVAL_TOOL_CALL_TYPE = "retrieval";
  static final String FUNCTION_TOOL_CALL_TYPE = "function";

  static final String CODE_INTERPRETER_LOG_OUTPUT_TYPE = "logs";
  static final String CODE_INTERPRETER_IMAGE_OUTPUT_TYPE = "image";

  static final String TEXT_CONTENT_PART_TYPE = "text";
  static final String IMAGE_CONTENT_PART_TYPE = "image_url";
}
