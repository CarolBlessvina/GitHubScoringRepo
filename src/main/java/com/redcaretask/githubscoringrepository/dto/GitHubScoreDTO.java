package com.redcaretask.githubscoringrepository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubScoreDTO {

    private String name;
    private Double score;

}
