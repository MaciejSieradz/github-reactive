package com.example.githubreactive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class RepositoryDTO {

    private String name;
    private String onwerLogin;

    private List<BranchDTO> branches;

}
