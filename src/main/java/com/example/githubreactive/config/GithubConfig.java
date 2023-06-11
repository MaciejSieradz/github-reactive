package com.example.githubreactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubConfig {

    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder().baseUrl("https://api.github.com");
    }
}
