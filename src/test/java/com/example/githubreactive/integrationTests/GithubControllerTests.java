package com.example.githubreactive.integrationTests;

import com.example.githubreactive.controller.GithubController;
import com.example.githubreactive.dto.BranchDTO;
import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.service.GitHubService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@WebFluxTest(GithubController.class)
public class GithubControllerTests {

    @MockBean
    private GitHubService gitHubService;

    @Autowired
    private WebTestClient testClient;

    @Test
    void shouldReturnResponse() {

        var repos = Flux.just(
                new RepositoryDTO("repo-one", "owner-one",
                        List.of(new BranchDTO("branch-one", "123"))),
                new RepositoryDTO("repo-two", "owner-two",
                        List.of(new BranchDTO("branch-one", "123"))));

        given(gitHubService.getUserRepositories(anyString())).willReturn(repos);

        testClient.get()
                .uri("/repositories/MaciejSieradz")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryDTO.class)
                .consumeWith( response -> {
                    Assertions.assertThat(response.getResponseBody()).isNotNull();
                    Assertions.assertThat(response.getResponseBody().hashCode()).isEqualTo(repos.collectList().block().hashCode());
                });

    }
}
