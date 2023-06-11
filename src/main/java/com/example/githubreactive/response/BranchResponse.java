package com.example.githubreactive.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BranchResponse {

    private String name;
    private Commit commit;
}
