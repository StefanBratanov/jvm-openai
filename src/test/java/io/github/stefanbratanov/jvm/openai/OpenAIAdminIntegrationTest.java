package io.github.stefanbratanov.jvm.openai;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.stefanbratanov.jvm.openai.UsersClient.PaginatedUsers;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
}
