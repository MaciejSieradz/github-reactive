package com.example.githubreactive.controller;

import com.example.githubreactive.dto.RepositoryDTO;
import com.example.githubreactive.service.GitHubService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class GithubController {

    private final GitHubService gitHubService;

    @GetMapping(path = "/repositories/{username}", produces = {"application/json"})
    public Flux<RepositoryDTO> getRepositories(@PathVariable String username) {

        return gitHubService.getUserRepositories(username);
    }

}
