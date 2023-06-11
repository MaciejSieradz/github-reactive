package com.example.githubreactive.dto;

import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
public final class BranchDTO {

    private String name;
    private String commitSha;

}
