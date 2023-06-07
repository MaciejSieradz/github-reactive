package com.example.githubreactive.integration;

import com.example.githubreactive.dto.ErrorResponse;
import com.example.githubreactive.dto.RepositoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class GithubControllerWebTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldReturnRepos() {
        webTestClient.get().uri("/repositories/MaciejSieradz")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryDTO.class).hasSize(8);
    }

    @Test
    public void shouldReturnNoUsernameResponse() {
        webTestClient.get().uri("/repositories/MaciejSieradzaa")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ErrorResponse.class);
    }

    @Test
    public void shouldReturnInvalidTypeResponse() {
        webTestClient.get().uri("/repositories/MaciejSieradzaa")
                .header("Accept", "application/xml")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody(ErrorResponse.class);
    }
}
