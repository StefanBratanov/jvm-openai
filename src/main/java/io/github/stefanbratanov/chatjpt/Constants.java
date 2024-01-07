package io.github.stefanbratanov.chatjpt;

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
}
