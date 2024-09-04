package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class AuditLogSerializer extends StdSerializer<AuditLog> {

  public AuditLogSerializer() {
    super(AuditLog.class);
  }

  @Override
  public void serialize(AuditLog value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeStartObject();

    gen.writeStringField("id", value.id());
    gen.writeStringField("type", value.type());
    gen.writeNumberField("effective_at", value.effectiveAt());

    if (value.project() != null) {
      gen.writeObjectField("project", value.project());
    }

    if (value.actor() != null) {
      gen.writeObjectField("actor", value.actor());
    }

    AuditLogEvent event = value.event();
    if (event != null) {
      switch (value.type()) {
        case Constants.API_KEY_CREATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.API_KEY_CREATED_EVENT_TYPE, event);
        case Constants.API_KEY_UPDATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.API_KEY_UPDATED_EVENT_TYPE, event);
        case Constants.API_KEY_DELETED_EVENT_TYPE ->
            gen.writeObjectField(Constants.API_KEY_DELETED_EVENT_TYPE, event);
        case Constants.INVITE_SENT_EVENT_TYPE ->
            gen.writeObjectField(Constants.INVITE_SENT_EVENT_TYPE, event);
        case Constants.INVITE_ACCEPTED_EVENT_TYPE ->
            gen.writeObjectField(Constants.INVITE_ACCEPTED_EVENT_TYPE, event);
        case Constants.INVITE_DELETED_EVENT_TYPE ->
            gen.writeObjectField(Constants.INVITE_DELETED_EVENT_TYPE, event);
        case Constants.LOGIN_FAILED_EVENT_TYPE ->
            gen.writeObjectField(Constants.LOGIN_FAILED_EVENT_TYPE, event);
        case Constants.LOGOUT_FAILED_EVENT_TYPE ->
            gen.writeObjectField(Constants.LOGOUT_FAILED_EVENT_TYPE, event);
        case Constants.ORGANIZATION_UPDATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.ORGANIZATION_UPDATED_EVENT_TYPE, event);
        case Constants.PROJECT_CREATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.PROJECT_CREATED_EVENT_TYPE, event);
        case Constants.PROJECT_UPDATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.PROJECT_UPDATED_EVENT_TYPE, event);
        case Constants.PROJECT_ARCHIVED_EVENT_TYPE ->
            gen.writeObjectField(Constants.PROJECT_ARCHIVED_EVENT_TYPE, event);
        case Constants.SERVICE_ACCOUNT_CREATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.SERVICE_ACCOUNT_CREATED_EVENT_TYPE, event);
        case Constants.SERVICE_ACCOUNT_UPDATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.SERVICE_ACCOUNT_UPDATED_EVENT_TYPE, event);
        case Constants.SERVICE_ACCOUNT_DELETED_EVENT_TYPE ->
            gen.writeObjectField(Constants.SERVICE_ACCOUNT_DELETED_EVENT_TYPE, event);
        case Constants.USER_ADDED_EVENT_TYPE ->
            gen.writeObjectField(Constants.USER_ADDED_EVENT_TYPE, event);
        case Constants.USER_UPDATED_EVENT_TYPE ->
            gen.writeObjectField(Constants.USER_UPDATED_EVENT_TYPE, event);
        case Constants.USER_DELETED_EVENT_TYPE ->
            gen.writeObjectField(Constants.USER_DELETED_EVENT_TYPE, event);
        default -> throw new IllegalArgumentException("Unexpected event type: " + value.type());
      }
    }

    gen.writeEndObject();
  }
}
