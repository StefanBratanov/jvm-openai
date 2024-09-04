package io.github.stefanbratanov.jvm.openai;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ApiKeyCreatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ApiKeyDeletedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ApiKeyUpdatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.InviteAcceptedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.InviteDeletedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.InviteSentEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.LoginFailedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.LogoutFailedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.OrganizationUpdatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ProjectArchivedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ProjectCreatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ProjectUpdatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ServiceAccountCreatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ServiceAccountDeletedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.ServiceAccountUpdatedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.UserAddedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.UserDeletedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.UserUpdatedEvent;
import java.io.IOException;

public class AuditLogDeserializer extends StdDeserializer<AuditLog> {

  public AuditLogDeserializer() {
    super(AuditLog.class);
  }

  @Override
  public AuditLog deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);

    String id = node.get("id").asText();
    String type = node.get("type").asText();
    long effectiveAt = node.get("effective_at").asLong();
    JsonNode projectNode = node.get("project");
    AuditLog.Project project =
        projectNode != null ? p.getCodec().treeToValue(projectNode, AuditLog.Project.class) : null;
    JsonNode actorNode = node.get("actor");
    AuditLog.Actor actor = p.getCodec().treeToValue(actorNode, AuditLog.Actor.class);

    AuditLogEvent event;

    switch (type) {
      case Constants.API_KEY_CREATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.API_KEY_CREATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ApiKeyCreatedEvent.class);
      }
      case Constants.API_KEY_UPDATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.API_KEY_UPDATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ApiKeyUpdatedEvent.class);
      }
      case Constants.API_KEY_DELETED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.API_KEY_DELETED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ApiKeyDeletedEvent.class);
      }
      case Constants.INVITE_SENT_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.INVITE_SENT_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, InviteSentEvent.class);
      }
      case Constants.INVITE_ACCEPTED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.INVITE_ACCEPTED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, InviteAcceptedEvent.class);
      }
      case Constants.INVITE_DELETED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.INVITE_DELETED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, InviteDeletedEvent.class);
      }
      case Constants.LOGIN_FAILED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.LOGIN_FAILED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, LoginFailedEvent.class);
      }
      case Constants.LOGOUT_FAILED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.LOGOUT_FAILED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, LogoutFailedEvent.class);
      }
      case Constants.ORGANIZATION_UPDATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.ORGANIZATION_UPDATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, OrganizationUpdatedEvent.class);
      }
      case Constants.PROJECT_CREATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.PROJECT_CREATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ProjectCreatedEvent.class);
      }
      case Constants.PROJECT_UPDATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.PROJECT_UPDATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ProjectUpdatedEvent.class);
      }
      case Constants.PROJECT_ARCHIVED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.PROJECT_ARCHIVED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ProjectArchivedEvent.class);
      }
      case Constants.SERVICE_ACCOUNT_CREATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.SERVICE_ACCOUNT_CREATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ServiceAccountCreatedEvent.class);
      }
      case Constants.SERVICE_ACCOUNT_UPDATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.SERVICE_ACCOUNT_UPDATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ServiceAccountUpdatedEvent.class);
      }
      case Constants.SERVICE_ACCOUNT_DELETED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.SERVICE_ACCOUNT_DELETED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, ServiceAccountDeletedEvent.class);
      }
      case Constants.USER_ADDED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.USER_ADDED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, UserAddedEvent.class);
      }
      case Constants.USER_UPDATED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.USER_UPDATED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, UserUpdatedEvent.class);
      }
      case Constants.USER_DELETED_EVENT_TYPE -> {
        JsonNode eventNode = node.get(Constants.USER_DELETED_EVENT_TYPE);
        event = p.getCodec().treeToValue(eventNode, UserDeletedEvent.class);
      }
      default -> event = null;
    }

    return new AuditLog(id, type, effectiveAt, project, actor, event);
  }
}
