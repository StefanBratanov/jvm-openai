package io.github.stefanbratanov.jvm.openai;

/** A log of a user action or configuration change within this organization. */
public record AuditLog(
    String id, String type, long effectiveAt, Project project, Actor actor, AuditLogEvent event) {

  /** The project that the action was scoped to. Absent for actions not scoped to projects. */
  public record Project(String id, String name) {}

  /** The actor who performed the audit logged action. */
  public record Actor(String type, Session session, ApiKey apiKey) {

    /** The session in which the audit logged action was performed. */
    public record Session(User user, String ipAddress) {
      /** The user who performed the audit logged action. */
      public record User(String id, String email) {}
    }

    /** The API Key used to perform the audit logged action. */
    public record ApiKey(String id, String type, User user, ServiceAccount serviceAccount) {
      /** The user who performed the audit logged action. */
      public record User(String id, String email) {}

      /** The service account that performed the audit logged action. */
      public record ServiceAccount(String id) {}
    }
  }
}
