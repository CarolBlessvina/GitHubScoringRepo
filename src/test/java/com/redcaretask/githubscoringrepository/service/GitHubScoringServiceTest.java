package com.redcaretask.githubscoringrepository.service;

import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryItemDTO;
import com.redcaretask.githubscoringrepository.dto.GitHubScoreDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class GitHubScoringServiceTest {
    @InjectMocks
    private GitHubScoringService gitHubScoringService;

    static Stream<Arguments> repositoryProvider() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new GitHubRepositoryItemDTO("awesome-java",
                                        "https://github.com/username/awesome-java",
                                        "A curated list of awesome Java frameworks, libraries, and software.",
                                        "Java",
                                        15,
                                        120,
                                        LocalDateTime.now().minusDays(3),
                                        LocalDateTime.of(2018, 5, 10, 14, 30))
                        ),
                        List.of(
                                new GitHubScoreDTO("awesome-java", 285.0) // Expected Score
                        )
                ),
                Arguments.of(
                        List.of(
                                new GitHubRepositoryItemDTO("python-scripts",
                                        "https://github.com/username/python-scripts",
                                        "Collection of useful Python scripts for automation and development.",
                                        "Python",
                                        0,
                                        0,
                                        LocalDateTime.now().minusDays(350),
                                        LocalDateTime.of(2019, 3, 15, 10, 45))
                        ),
                        List.of(
                                new GitHubScoreDTO("python-scripts", 0.0) // Expected Score
                        )
                ),
                Arguments.of(
                        List.of(
                                new GitHubRepositoryItemDTO("react-ui-kit",
                                        "https://github.com/username/react-ui-kit",
                                        "A UI component library for React.",
                                        "JavaScript",
                                        50,
                                        250,
                                        LocalDateTime.now().minusDays(10),
                                        LocalDateTime.of(2020, 1, 5, 9, 0))
                        ),
                        List.of(
                                new GitHubScoreDTO("react-ui-kit", 650.0) // Expected Score
                        )
                ),
                Arguments.of(
                        List.of(
                                new GitHubRepositoryItemDTO("data-structures",
                                        "https://github.com/username/data-structures",
                                        "Common data structures implemented in C++.",
                                        "C++",
                                        5,
                                        50,
                                        LocalDateTime.now().minusDays(100),
                                        LocalDateTime.of(2017, 8, 20, 16, 45))
                        ),
                        List.of(
                                new GitHubScoreDTO("data-structures", 115.0) // Expected Score
                        )
                ),
                Arguments.of(
                        List.of(
                                new GitHubRepositoryItemDTO("ruby-gems",
                                        "https://github.com/username/ruby-gems",
                                        "A collection of useful Ruby gems for web development.",
                                        "Ruby",
                                        30,
                                        300,
                                        LocalDateTime.now().minusDays(5),
                                        LocalDateTime.of(2016, 6, 30, 12, 15))
                        ),
                        List.of(
                                new GitHubScoreDTO("ruby-gems", 690.0) // Expected Score
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("repositoryProvider")
    @DisplayName("Should calculate scores for different repositories")
    void shouldCalculateScore(List<GitHubRepositoryItemDTO> repositories, List<GitHubScoreDTO> expectedScores) {
        // WHEN
        List<GitHubScoreDTO> scores = gitHubScoringService.applyScoring(repositories);

        // THEN
        assertEquals(expectedScores.size(), scores.size());

        for (int i = 0; i < expectedScores.size(); i++) {
            assertEquals(expectedScores.get(i).getName(), scores.get(i).getName());
            assertEquals(expectedScores.get(i).getScore(), scores.get(i).getScore());
        }
    }

    @Test
    @DisplayName("Should return an empty list when no repositories are provided")
    void shouldReturnEmptyListWhenNoRepositories() {
        // GIVEN
        List<GitHubRepositoryItemDTO> emptyList = List.of();

        // WHEN
        List<GitHubScoreDTO> scores = gitHubScoringService.applyScoring(emptyList);

        // THEN
        assertEquals(0, scores.size());
    }
}
