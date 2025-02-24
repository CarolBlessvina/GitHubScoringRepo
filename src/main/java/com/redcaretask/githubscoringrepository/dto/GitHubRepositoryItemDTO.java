package com.redcaretask.githubscoringrepository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class GitHubRepositoryItemDTO {
    private String name;

    @JsonProperty("html_url")
    private String url;

    private String description;

    private String language;

    private int forks;

    @JsonProperty("stargazers_count")
    private int stars;

    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @JsonProperty("updated_at")
    private LocalDateTime updatedSince;
}
