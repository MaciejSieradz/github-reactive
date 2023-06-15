package com.example.githubreactive.e2eTests;

import com.example.githubreactive.dto.RepositoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 6060)
@AutoConfigureWebTestClient
public class GithubControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebClient.Builder webClient;

    @BeforeEach
    public void setWebClient() {
        this.webClient.baseUrl("http://localhost:6060");
    }

    @Test
    void shouldReturRepos() throws IOException {
        final var classLoader = getClass().getClassLoader();

        final var resourceName = "correct-response.json";
        final var resourceBranchName = "correct-branch-response.json";

        final var githubRepos = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
        final var githubBranches = new File(Objects.requireNonNull(classLoader.getResource(resourceBranchName)).getFile());
        final var correctResponse = new File(Objects.requireNonNull(classLoader.getResource("expected-response.json")).getFile());

        var responseGithubReposBody = IOUtils.toString(githubRepos.toURI(), StandardCharsets.UTF_8);
        var responseGithubBranchBody = IOUtils.toString(githubBranches.toURI(), StandardCharsets.UTF_8);

        var objectMapper = new ObjectMapper();

        var expectedResponse = List.of(objectMapper.readValue(correctResponse, RepositoryDTO[].class));

        stubFor(get(urlPathEqualTo("/users/MaciejSieradz/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(responseGithubReposBody)));

        stubFor(get(urlMatching("/repos/Lokinado/flutter_inventory_app/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseGithubBranchBody)));

        webTestClient.get().uri("/repositories/MaciejSieradz")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryDTO.class)
                .consumeWith(response -> {
                    Assertions.assertThat(response.getResponseBody()).isNotNull();
                    Assertions.assertThat(response.getResponseBody().hashCode()).isEqualTo(expectedResponse.hashCode());
                });
    }
}
