package com.example.githubreactive.unitTests;

import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.exception.UsernameNotFoundException;
import com.example.githubreactive.service.GitHubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@ExtendWith(SpringExtension.class)
@WireMockTest(httpsEnabled = true)
public class GithubServiceTests {

    private GitHubService gitHubService;

    @BeforeEach
    public void init(WireMockRuntimeInfo wireMockRuntimeInfo) {

        int port = wireMockRuntimeInfo.getHttpPort();

        WebClient.Builder webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port);

        gitHubService = new GitHubService(webClient);
    }

    @Test
    void shouldReturnRepos() throws IOException {

        final var classLoader = getClass().getClassLoader();

        final var resourceName = "correct-response.json";
        final var resourceBranchName = "correct-branch-response.json";

        final var githubRepos = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
        final var githubBranches = new File(Objects.requireNonNull(classLoader.getResource(resourceBranchName)).getFile());
        final var correctResponse = new File(Objects.requireNonNull(classLoader.getResource("expected-response.json")).getFile());

        var responseGithubReposBody = IOUtils.toString(githubRepos.toURI(), StandardCharsets.UTF_8);
        var responseGithubBranchBody = IOUtils.toString(githubBranches.toURI(), StandardCharsets.UTF_8);


        stubFor(get(urlPathEqualTo("/users/MaciejSieradz/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseGithubReposBody)));

        stubFor(get(urlMatching("/repos/Lokinado/flutter_inventory_app/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseGithubBranchBody)));

        var response = gitHubService.getUserRepositories("MaciejSieradz");

        var repos = response.collectList().block();

        var objectMapper = new ObjectMapper();

        var expectedResponse = List.of(objectMapper.readValue(correctResponse, RepositoryDTO[].class));

        Assertions.assertThat(repos).isNotNull();
        Assertions.assertThat(repos.size()).isEqualTo(1);
        Assertions.assertThat(repos.hashCode()).isEqualTo(expectedResponse.hashCode());
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        stubFor(get(urlPathEqualTo("/users/invalid-username/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("404 Not found")));

        var result = gitHubService.getUserRepositories("invalid-username");

        StepVerifier.create(result)
                        .expectError(UsernameNotFoundException.class)
                        .verify();

    }

}
