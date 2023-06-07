package com.example.githubreactive.integration;

import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.exception.UsernameNotFoundException;
import com.example.githubreactive.service.GitHubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class GitHubServiceTest {

    @Test
    void getUserRepositories() {
        GitHubService service = new GitHubService(WebClient.create());
        String username = "MaciejSieradz";

        Flux<RepositoryDTO> repositories = service.getUserRepositories(username);

        int expectedNumberOfRepositories = 8;

        Assertions.assertEquals(expectedNumberOfRepositories, repositories.count().block());
    }

    @Test
    void returnUsernameNotFoundException() {
        GitHubService service = new GitHubService(WebClient.create());
        String username = "MaciejSieradzaa";

        Flux<RepositoryDTO> repositories = service.getUserRepositories(username);

        StepVerifier.create(repositories)
                .expectErrorMatches(throwable -> throwable instanceof UsernameNotFoundException).verify();
    }
}