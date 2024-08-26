package io.github.stefanbratanov.jvm.openai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manage the projects within an organization includes creation, updating, and archiving or
 * projects. The Default project cannot be modified or archived.
 *
 * <p>Based on <a href="https://platform.openai.com/docs/api-reference/projects">Projects</a>
 */
public final class ProjectsClient extends OpenAIClient {

  private final URI baseUrl;

  ProjectsClient(
      URI baseUrl,
      String[] authenticationHeaders,
      HttpClient httpClient,
      Optional<Duration> requestTimeout) {
    super(authenticationHeaders, httpClient, requestTimeout);
    this.baseUrl = baseUrl;
  }

  /**
   * Returns a list of projects.
   *
   * @param after A cursor for use in pagination. after is an object ID that defines your place in
   *     the list.
   * @param limit A limit on the number of objects to be returned.
   * @param includeArchived If true returns all projects including those that have been archived.
   *     Archived projects are not included by default.
   * @throws OpenAIException in case of API errors
   */
  public PaginatedProjects listProjects(
      Optional<String> after, Optional<Integer> limit, Optional<Boolean> includeArchived) {
    String queryParameters =
        createQueryParameters(
            Map.of(
                Constants.LIMIT_QUERY_PARAMETER,
                limit,
                Constants.AFTER_QUERY_PARAMETER,
                after,
                "include_archived",
                includeArchived));
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath() + queryParameters))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), PaginatedProjects.class);
  }

  public record PaginatedProjects(
      List<Project> data, String firstId, String lastId, boolean hasMore) {}

  /**
   * Create a new project in the organization. Projects can be created and archived, but cannot be
   * deleted.
   *
   * @throws OpenAIException in case of API errors
   */
  public Project createProject(CreateProjectRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath()))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Project.class);
  }

  /**
   * Retrieves a project.
   *
   * @param projectId The ID of the project.
   * @throws OpenAIException in case of API errors
   */
  public Project retrieveProject(String projectId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath() + "/" + projectId))
            .GET()
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Project.class);
  }

  /**
   * Modifies a project in the organization.
   *
   * @param projectId The ID of the project.
   * @throws OpenAIException in case of API errors
   */
  public Project modifyProject(String projectId, ModifyProjectRequest request) {
    HttpRequest httpRequest =
        newHttpRequestBuilder(Constants.CONTENT_TYPE_HEADER, Constants.JSON_MEDIA_TYPE)
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath() + "/" + projectId))
            .POST(createBodyPublisher(request))
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Project.class);
  }

  /**
   * Archives a project in the organization. Archived projects cannot be used or updated.
   *
   * @param projectId The ID of the project.
   * @throws OpenAIException in case of API errors
   */
  public Project archiveProject(String projectId) {
    HttpRequest httpRequest =
        newHttpRequestBuilder()
            .uri(baseUrl.resolve(Endpoint.PROJECTS.getPath() + "/" + projectId + "/archive"))
            .POST(BodyPublishers.noBody())
            .build();
    HttpResponse<byte[]> httpResponse = sendHttpRequest(httpRequest);
    return deserializeResponse(httpResponse.body(), Project.class);
  }
}
