package io.github.stefanbratanov.jvm.openai;

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
import java.util.List;

/** The details for the different types of audit log events */
public sealed interface AuditLogEvent
    permits ApiKeyCreatedEvent,
        ApiKeyUpdatedEvent,
        ApiKeyDeletedEvent,
        InviteSentEvent,
        InviteAcceptedEvent,
        InviteDeletedEvent,
        LoginFailedEvent,
        LogoutFailedEvent,
        OrganizationUpdatedEvent,
        ProjectCreatedEvent,
        ProjectUpdatedEvent,
        ProjectArchivedEvent,
        ServiceAccountCreatedEvent,
        ServiceAccountUpdatedEvent,
        ServiceAccountDeletedEvent,
        UserAddedEvent,
        UserUpdatedEvent,
        UserDeletedEvent {

  record ApiKeyCreatedEvent(String id, Data data) implements AuditLogEvent {
    public record Data(List<String> scopes) {}
  }

  record ApiKeyUpdatedEvent(String id, ChangesRequested changesRequested) implements AuditLogEvent {
    public record ChangesRequested(List<String> scopes) {}
  }

  record ApiKeyDeletedEvent(String id) implements AuditLogEvent {}

  record InviteSentEvent(String id, Data data) implements AuditLogEvent {
    public record Data(String email, String role) {}
  }

  record InviteAcceptedEvent(String id) implements AuditLogEvent {}

  record InviteDeletedEvent(String id) implements AuditLogEvent {}

  record LoginFailedEvent(String errorCode, String errorMessage) implements AuditLogEvent {}

  record LogoutFailedEvent(String errorCode, String errorMessage) implements AuditLogEvent {}

  record OrganizationUpdatedEvent(String id, ChangesRequested changesRequested)
      implements AuditLogEvent {
    public record ChangesRequested(
        String title, String description, String name, Settings settings) {
      public record Settings(String threadsUiVisibility, String usageDashboardVisibility) {}
    }
  }

  record ProjectCreatedEvent(String id, Data data) implements AuditLogEvent {
    public record Data(String name, String title) {}
  }

  record ProjectUpdatedEvent(String id, ChangesRequested changesRequested)
      implements AuditLogEvent {
    public record ChangesRequested(String title) {}
  }

  record ProjectArchivedEvent(String id) implements AuditLogEvent {}

  record ServiceAccountCreatedEvent(String id, Data data) implements AuditLogEvent {
    public record Data(String role) {}
  }

  record ServiceAccountUpdatedEvent(String id, ChangesRequested changesRequested)
      implements AuditLogEvent {
    public record ChangesRequested(String role) {}
  }

  record ServiceAccountDeletedEvent(String id) implements AuditLogEvent {}

  record UserAddedEvent(String id, Data data) implements AuditLogEvent {
    public record Data(String role) {}
  }

  record UserUpdatedEvent(String id, ChangesRequested changesRequested) implements AuditLogEvent {
    public record ChangesRequested(String role) {}
  }

  record UserDeletedEvent(String id) implements AuditLogEvent {}
}
