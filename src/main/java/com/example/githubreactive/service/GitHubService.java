package com.example.githubreactive.service;

import com.example.githubreactive.dto.BranchDTO;
import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.exception.UsernameNotFoundException;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * GitHubService
 */
@Service
@AllArgsConstructor
public class GitHubService {

    private final WebClient webClient;

    public Flux<RepositoryDTO> getUserRepositories(String username) {
        return webClient
                .get()
                .uri("https://api.github.com/users/{username}/repos?type=all", username)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                        return Mono.error(new UsernameNotFoundException(String.format("Username: %s not found.", username)));
                    } else {
                        return clientResponse.bodyToMono(String.class);
                    }
                })
                .flatMapMany(this::returnFluxRepositoryFromResponse);
    }

    private Flux<RepositoryDTO> returnFluxRepositoryFromResponse(String response) {
        List<String> repositoryNames =
                JsonPath.parse(response).read("$[?(@.fork == false)].name");
        List<String> repositoryOwners =
                JsonPath.parse(response).read("$[?(@.fork == false)].owner.login");

        return Flux.fromIterable(repositoryNames).zipWith(Flux.fromIterable(repositoryOwners))
                .flatMap(tuple ->
                {
                    String repositoryName = tuple.getT1();
                    String repositoryOwner = tuple.getT2();

                    Mono<List<BranchDTO>> branchesMono = webClient
                            .get()
                            .uri("https://api.github.com/repos/{owner}/{repo}/branches", repositoryOwner, repositoryName)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(this::createBranchesFromResponse);

                    return branchesMono.map(branches -> new RepositoryDTO(repositoryName, repositoryOwner, branches));
                });
    }

    private List<BranchDTO> createBranchesFromResponse(String branchesResponse) {
        List<String> branchNames = JsonPath.parse(branchesResponse).read("$[*].name");
        List<String> commitShas = JsonPath.parse(branchesResponse).read("$[*].commit.sha");

        List<BranchDTO> branches = new ArrayList<>();

        for (int i = 0; i < branchNames.size(); i++) {
            branches.add(new BranchDTO(branchNames.get(i), commitShas.get(i)));
        }

        return branches;
    }

}
