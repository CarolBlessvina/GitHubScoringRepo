package com.redcaretask.githubscoringrepository.controller;

import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryItemDTO;
import com.redcaretask.githubscoringrepository.dto.GitHubScoreDTO;
import com.redcaretask.githubscoringrepository.service.GitHubClient;
import com.redcaretask.githubscoringrepository.service.GitHubScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/github", "/api/v1/github"})
public class GitHubController {

    private final GitHubClient gitHubClient;
    private final GitHubScoringService gitHubScoringService;

    public GitHubController(GitHubClient gitHubClient, GitHubScoringService gitHubScoringService) {
        this.gitHubClient = gitHubClient;
        this.gitHubScoringService = gitHubScoringService;
    }

    @GetMapping("/repositories/score")
    public ResponseEntity<List<GitHubScoreDTO>> getRepositoriesScored(
            @RequestParam LocalDateTime creationDate, //2025-02-23T12:00
            @RequestParam String language) {

        List<GitHubRepositoryItemDTO> repositories = gitHubClient.fetchRepositories(creationDate, language);
        return ResponseEntity.ok(gitHubScoringService.applyScoring(repositories));
    }
}
