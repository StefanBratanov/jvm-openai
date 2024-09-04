package io.github.stefanbratanov.jvm.openai;

class Constants {

  private Constants() {}

  static final String JSON_MEDIA_TYPE = "application/json";
  static final String CONTENT_TYPE_HEADER = "Content-Type";
  static final String ACCEPT_HEADER = "Accept";
  static final String AUTHORIZATION_HEADER = "Authorization";

  static final String OPENAI_ORGANIZATION_HEADER = "OpenAI-Organization";
  static final String OPENAI_PROJECT_HEADER = "OpenAI-Project";
  static final String OPENAI_BETA_HEADER = "OpenAI-Beta";

  static final String IMAGE_FILE_MESSAGE_CONTENT_TYPE = "image_file";
  static final String IMAGE_URL_MESSAGE_CONTENT_TYPE = "image_url";
  static final String TEXT_MESSAGE_CONTENT_TYPE = "text";

  static final String FILE_CITATION_ANNOTATION_TYPE = "file_citation";
  static final String FILE_PATH_ANNOTATION_TYPE = "file_path";

  static final String CODE_INTERPRETER_TOOL_TYPE = "code_interpreter";
  static final String FILE_SEARCH_TOOL_TYPE = "file_search";
  static final String FUNCTION_TOOL_TYPE = "function";

  static final String MESSAGE_CREATION_STEP_DETAILS_TYPE = "message_creation";
  static final String TOOL_CALLS_STEP_DETAILS_TYPE = "tool_calls";

  static final String CODE_INTERPRETER_TOOL_CALL_TYPE = "code_interpreter";
  static final String FILE_SEARCH_TOOL_CALL_TYPE = "file_search";
  static final String FUNCTION_TOOL_CALL_TYPE = "function";

  static final String CODE_INTERPRETER_LOG_OUTPUT_TYPE = "logs";
  static final String CODE_INTERPRETER_IMAGE_OUTPUT_TYPE = "image";

  static final String TEXT_CONTENT_PART_TYPE = "text";
  static final String IMAGE_URL_CONTENT_PART_TYPE = "image_url";
  static final String IMAGE_FILE_CONTENT_PART_TYPE = "image_file";

  static final String SUBMIT_TOOL_OUTPUTS_REQUIRED_ACTION_TYPE = "submit_tool_outputs";

  static final String WANDB_INTEGRATION_TYPE = "wandb";

  static final String LIMIT_QUERY_PARAMETER = "limit";
  static final String AFTER_QUERY_PARAMETER = "after";
  static final String BEFORE_QUERY_PARAMETER = "before";
  static final String INCLUDE_QUERY_PARAMETER = "include[]";

  static final String AUTO_CHUNKING_STRATEGY = "auto";
  static final String STATIC_CHUNKING_STRATEGY = "static";
  static final String OTHER_CHUNKING_STRATEGY = "other";

  static final String API_KEY_CREATED_EVENT_TYPE = "api_key.created";
  static final String API_KEY_UPDATED_EVENT_TYPE = "api_key.updated";
  static final String API_KEY_DELETED_EVENT_TYPE = "api_key.deleted";
  static final String INVITE_SENT_EVENT_TYPE = "invite.sent";
  static final String INVITE_ACCEPTED_EVENT_TYPE = "invite.accepted";
  static final String INVITE_DELETED_EVENT_TYPE = "invite.deleted";
  static final String LOGIN_FAILED_EVENT_TYPE = "login.failed";
  static final String LOGOUT_FAILED_EVENT_TYPE = "logout.failed";
  static final String ORGANIZATION_UPDATED_EVENT_TYPE = "organization.updated";
  static final String PROJECT_CREATED_EVENT_TYPE = "project.created";
  static final String PROJECT_UPDATED_EVENT_TYPE = "project.updated";
  static final String PROJECT_ARCHIVED_EVENT_TYPE = "project.archived";
  static final String SERVICE_ACCOUNT_CREATED_EVENT_TYPE = "service_account.created";
  static final String SERVICE_ACCOUNT_UPDATED_EVENT_TYPE = "service_account.updated";
  static final String SERVICE_ACCOUNT_DELETED_EVENT_TYPE = "service_account.deleted";
  static final String USER_ADDED_EVENT_TYPE = "user.added";
  static final String USER_UPDATED_EVENT_TYPE = "user.updated";
  static final String USER_DELETED_EVENT_TYPE = "user.deleted";
}
