package com.redcaretask.githubscoringrepository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepositoryDTO {

    @JsonProperty("total_count")
    private String totalCount;

    @JsonProperty("incomplete_results")
    private String incompleteResults;

    private List<GitHubRepositoryItemDTO> items;

}
