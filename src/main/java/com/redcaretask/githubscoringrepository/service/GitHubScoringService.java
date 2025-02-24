package com.redcaretask.githubscoringrepository.service;


import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryItemDTO;
import com.redcaretask.githubscoringrepository.dto.GitHubScoreDTO;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GitHubScoringService {

    private static final int STAR_WEIGHT = 2;
    private static final int FORK_WEIGHT = 3;

    @Timed
    public List<GitHubScoreDTO> applyScoring(List<GitHubRepositoryItemDTO> repositories) {
        log.info("Size of repositories that are being scored {}", repositories.size());

        return repositories
                .stream()
                .map(repository -> {
                    double score = calculateScore(repository.getStars(), repository.getForks(), repository.getUpdatedSince());
                    return new GitHubScoreDTO(repository.getName(), score);
                })
                .collect(Collectors.toList());
    }

    private double calculateScore(int stars, int forks, LocalDateTime lastUpdated) {

        long daysSinceUpdate = ChronoUnit.DAYS.between(lastUpdated, LocalDateTime.now());
        double recencyBonus = Math.max(100 - daysSinceUpdate, 0); // TODO constants

        return (stars * STAR_WEIGHT) + (forks * FORK_WEIGHT) + recencyBonus;
    }
}

