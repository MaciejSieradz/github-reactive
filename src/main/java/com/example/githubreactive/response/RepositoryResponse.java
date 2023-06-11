package com.example.githubreactive.response;

import lombok.*;

@NoArgsConstructor
@Data
public final class RepositoryResponse {

    private  String name;
    private Owner owner;
    private boolean fork;
    private String branches_url;
}
