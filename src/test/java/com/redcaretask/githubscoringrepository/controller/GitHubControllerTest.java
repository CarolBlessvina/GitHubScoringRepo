package com.redcaretask.githubscoringrepository.controller;

import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryItemDTO;
import com.redcaretask.githubscoringrepository.dto.GitHubScoreDTO;
import com.redcaretask.githubscoringrepository.exception.GithubServiceUnavailableException;
import com.redcaretask.githubscoringrepository.service.GitHubClient;
import com.redcaretask.githubscoringrepository.service.GitHubScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GitHubController.class)
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubClient gitHubClient;

    @MockitoBean
    private GitHubScoringService gitHubScoringService;

    private List<GitHubRepositoryItemDTO> mockRepositories;
    private List<GitHubScoreDTO> mockScores;

    @BeforeEach
    void setUp() {
        mockRepositories = List.of(
                new GitHubRepositoryItemDTO("awesome-java",
                        "https://github.com/username/awesome-java",
                        "A curated list of awesome Java frameworks, libraries, and software.",
                        "Java",
                        15,
                        120,
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.of(2018, 5, 10, 14, 30))
        );

        mockScores = List.of(
                new GitHubScoreDTO("awesome-java", 285.0)
        );
    }

    @Test
    @DisplayName("Should return repository scores for given creation date and language")
    void shouldReturnRepositoryScores() throws Exception {
        Mockito.when(gitHubClient.fetchRepositories(any(LocalDateTime.class), any(String.class)))
                .thenReturn(mockRepositories);
        Mockito.when(gitHubScoringService.applyScoring(mockRepositories))
                .thenReturn(mockScores);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/repositories/score")
                        .param("creationDate", "2018-05-10T14:30:00")
                        .param("language", "Java"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("awesome-java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].score").value(285.0));
    }

    @Test
    @DisplayName("Should return GitHubService Unavailable when GitHub is Down")
    void shouldGitHubAPIUnavailable() throws Exception {
        Mockito.when(gitHubClient.fetchRepositories(any(LocalDateTime.class), any(String.class)))
                .thenThrow(new GithubServiceUnavailableException("GitHub API is down"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/repositories/score")
                        .param("creationDate", "2018-05-10T14:30:00")
                        .param("language", "Java"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Should return exception when query parameters are missing")
    void shouldReturnExceptionWhenParametersAreMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/repositories/score"))
                .andExpect(status().is5xxServerError());
    }
}
