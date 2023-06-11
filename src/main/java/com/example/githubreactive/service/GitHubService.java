package com.example.githubreactive.service;

import com.example.githubreactive.dto.BranchDTO;
import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.exception.UsernameNotFoundException;
import com.example.githubreactive.response.BranchResponse;
import com.example.githubreactive.response.RepositoryResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class GitHubService {

    private final WebClient.Builder webClient;

    public Flux<RepositoryDTO> getUserRepositories(String username) {
        return webClient.build()
                .get()
                .uri("/users/{username}/repos?type=all", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new UsernameNotFoundException(String.format("Username %s not found", username))))
                .bodyToFlux(RepositoryResponse.class)
                .filter(repositoryResponse -> !repositoryResponse.isFork())
                .flatMap(repositoryResponse -> webClient.build()
                        .get()
                        .uri("/repos/{owner}/{repo}/branches",
                                repositoryResponse.getOwner().getLogin(), repositoryResponse.getName())
                        .retrieve()
                        .bodyToMono(BranchResponse[].class)
                        .map(branchResponse -> {
                            var branches = Stream.of(branchResponse).
                                    map(branch -> new BranchDTO(branch.getName(), branch.getCommit().getSha())).collect(Collectors.toList());

                            return new RepositoryDTO(repositoryResponse.getName(), repositoryResponse.getOwner().getLogin(), branches);
                        }));
    }
}
