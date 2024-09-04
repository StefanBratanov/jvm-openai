package io.github.stefanbratanov.jvm.openai;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.stefanbratanov.jvm.openai.AuditLogEvent.InviteDeletedEvent;
import io.github.stefanbratanov.jvm.openai.AuditLogEvent.InviteSentEvent;
import io.github.stefanbratanov.jvm.openai.ProjectServiceAccountsClient.ProjectServiceAccountCreateResponse;
import io.github.stefanbratanov.jvm.openai.UsersClient.PaginatedUsers;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_ADMIN_KEY", matches = ".*\\S.*")
class OpenAIAdminIntegrationTest {

  private static OpenAI openAI;

  @BeforeAll
  public static void setUp() {
    String adminKey = System.getenv("OPENAI_ADMIN_KEY");
    openAI = OpenAI.newBuilder().adminKey(adminKey).build();
  }

  @Test
  void testInvitesClient() {
    InvitesClient invitesClient = openAI.invitesClient();

    InviteRequest inviteRequest =
        InviteRequest.newBuilder().email("foobar@example.com").role("reader").build();

    Invite invite = invitesClient.createInvite(inviteRequest);

    assertThat(invite.email()).isEqualTo("foobar@example.com");
    assertThat(invite.status()).isEqualTo("pending");
    assertThat(invite.expiresAt()).isGreaterThan(Instant.now().getEpochSecond());

    List<Invite> invites = invitesClient.listInvites(Optional.empty(), Optional.empty()).data();

    assertThat(invites).hasSize(1).containsExactly(invite);

    Invite retrievedInvite = invitesClient.retrieveInvite(invite.id());

    assertThat(retrievedInvite).isEqualTo(invite);

    // cleanup
    DeletionStatus deletionStatus = invitesClient.deleteInvite(invite.id());
    assertThat(deletionStatus.deleted()).isTrue();
  }

  @Test
  void testUsersClient() {
    UsersClient usersClient = openAI.usersClient();

    PaginatedUsers paginatedUsers = usersClient.listUsers(Optional.empty(), Optional.empty());

    assertThat(paginatedUsers.hasMore()).isFalse();
    assertThat(paginatedUsers.firstId()).isNotBlank();
    assertThat(paginatedUsers.lastId()).isNotBlank();

    List<User> users = paginatedUsers.data();

    assertThat(users).isNotEmpty();

    User user = users.get(0);

    User retrievedUser = usersClient.retrieveUser(user.id());

    assertThat(retrievedUser).isEqualTo(user);
  }

  @Test
  void testProjectsClient() {
    ProjectsClient projectsClient = openAI.projectsClient();

    List<Project> projects =
        projectsClient.listProjects(Optional.empty(), Optional.empty(), Optional.empty()).data();

    assertThat(projects).isNotEmpty();

    Project project = projects.get(0);

    assertThat(project.name()).isNotBlank();

    Project retrievedProject = projectsClient.retrieveProject(project.id());

    assertThat(retrievedProject).isEqualTo(project);
  }

  @Test
  void testProjectUsersClient() {
    ProjectUsersClient projectUsersClient = openAI.projectUsersClient();

    Project project = retrieveProject();

    List<ProjectUser> projectUsers =
        projectUsersClient
            .listProjectUsers(project.id(), Optional.empty(), Optional.empty())
            .data();

    assertThat(projectUsers).isNotEmpty();

    ProjectUser projectUser = projectUsers.get(0);

    ProjectUser retrievedProjectUser =
        projectUsersClient.retrieveProjectUser(project.id(), projectUser.id());

    assertThat(retrievedProjectUser).isEqualTo(projectUser);
  }

  @Test
  void testProjectServiceAccountsClient() {
    ProjectServiceAccountsClient projectServiceAccountsClient =
        openAI.projectServiceAccountsClient();

    Project project = retrieveProject();

    CreateProjectServiceAccountRequest createRequest =
        CreateProjectServiceAccountRequest.newBuilder()
            .name("foobar" + System.currentTimeMillis())
            .build();

    ProjectServiceAccountCreateResponse createResponse =
        projectServiceAccountsClient.createProjectServiceAccount(project.id(), createRequest);

    assertThat(createResponse.name()).isEqualTo(createRequest.name());
    assertThat(createResponse.apiKey()).isNotNull();

    ProjectServiceAccount retrievedProjectServiceAccount =
        projectServiceAccountsClient.retrieveProjectServiceAccount(
            project.id(), createResponse.id());

    assertThat(retrievedProjectServiceAccount.id()).isEqualTo(createResponse.id());
    assertThat(retrievedProjectServiceAccount.role()).isEqualTo(createResponse.role());

    // cleanup
    DeletionStatus deletionStatus =
        projectServiceAccountsClient.deleteProjectServiceAccount(project.id(), createResponse.id());

    assertThat(deletionStatus.deleted()).isTrue();
  }

  @Test
  void testProjectApiKeysClient() {
    ProjectApiKeysClient projectApiKeysClient = openAI.projectApiKeysClient();

    Project project = retrieveProject();

    List<ProjectApiKey> projectApiKeys =
        projectApiKeysClient
            .listProjectApiKeys(project.id(), Optional.empty(), Optional.empty())
            .data();

    assertThat(projectApiKeys).isNotEmpty();

    ProjectApiKey projectApiKey = projectApiKeys.get(0);

    ProjectApiKey retrievedProjectApiKey =
        projectApiKeysClient.retrieveProjectApiKey(project.id(), projectApiKey.id());

    assertThat(retrievedProjectApiKey).isEqualTo(projectApiKey);
  }

  @Test
  void testAuditLogsClient() {
    AuditLogsClient auditLogsClient = openAI.auditLogsClient();

    ListAuditLogsQueryParameters queryParameters =
        ListAuditLogsQueryParameters.newBuilder()
            .eventTypes(List.of("invite.sent", "invite.deleted"))
            .build();

    List<AuditLog> auditLogs = auditLogsClient.listAuditLogs(queryParameters).data();

    assertThat(auditLogs.stream().map(AuditLog::type).collect(Collectors.toSet())).hasSize(2);

    auditLogs.forEach(
        auditLog -> {
          switch (auditLog.type()) {
            case "invite.sent" -> assertThat(auditLog.event()).isInstanceOf(InviteSentEvent.class);
            case "invite.deleted" ->
                assertThat(auditLog.event()).isInstanceOf(InviteDeletedEvent.class);
            default -> Assertions.fail("Unexpected event type: " + auditLog.type());
          }
        });
  }

  private Project retrieveProject() {
    return openAI
        .projectsClient()
        .listProjects(Optional.empty(), Optional.empty(), Optional.empty())
        .data()
        .get(0);
  }
}
