package com.example.githubreactive.dto;

import java.util.List;

/**
 * RepositoryDTO
 */

public record RepositoryDTO(String name, String owner, List<BranchDTO> branches) {

}
