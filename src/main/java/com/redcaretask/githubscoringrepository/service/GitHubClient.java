package com.redcaretask.githubscoringrepository.service;

import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryDTO;
import com.redcaretask.githubscoringrepository.dto.GitHubRepositoryItemDTO;
import com.redcaretask.githubscoringrepository.exception.GithubBadRequestException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class GitHubClient {

    private static final int MAX_RESULT_LIMIT = 1000;
    private static final int MAX_RESULT_LIMIT_PER_PAGE = 100;
    private static final String GITHUB_APPLICATION_VND_GITHUB_TEXT_MATCH_JSON_HEADER = "application/vnd.github+json";

    private final RestTemplate restTemplate;
    private final String githubUrl;
    private final String githubToken;

    public GitHubClient(RestTemplate restTemplate, @Value("${github.api.url}") String githubUrl, @Value("${github.token}") String githubToken) {
        this.restTemplate = restTemplate;
        this.githubUrl = githubUrl;
        this.githubToken = githubToken;
    }

    @Timed
    public List<GitHubRepositoryItemDTO> fetchRepositories(LocalDateTime creationDate, String language) {
        log.info("Fetching repositories for language: {} from: {}", language, creationDate);
        List<GitHubRepositoryItemDTO> allRepositories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int page = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            log.info("Fetching page {} for language {}", page, language);
            GitHubRepositoryDTO response  = callGithubRepository(creationDate, language, page);
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                int totalCount = Integer.parseInt(response.getTotalCount());
                log.debug("Received {} repositories on page {}", response.getItems().size(), page);

                if (totalCount >= MAX_RESULT_LIMIT) {
                    log.info("Total results exceed limit. The total count is {} Fetching repositories on hourly basis.", totalCount);
                    allRepositories.addAll(fetchHourlyRepositories(creationDate, now, language));
                    break;
                }
                else {
                    allRepositories.addAll(response.getItems());

                    if (response.getItems().size() < 100) {
                        hasMoreData = false;
                    }
                }
                page++;
            }
        }
        log.info("Fetched a total of {} repositories.", allRepositories.size());
        return allRepositories;
    }

    private List<GitHubRepositoryItemDTO> fetchHourlyRepositories(LocalDateTime creationDate, LocalDateTime now, String language) {
        log.info("Fetching hourly repositories from {} to {} for language {}", creationDate, now, language);
        List<GitHubRepositoryItemDTO> hourlyRepositories = new ArrayList<>();

        while (creationDate.isBefore(now)) {
            int page = 1;
            boolean hasMoreData = true;

            while (hasMoreData) {
                log.info("Fetching page {} for hourly interval starting at {}", page, creationDate);
                GitHubRepositoryDTO response = callGithubRepository(creationDate, creationDate.plusHours(1), language, page);

                if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                    log.debug("Received {} repositories on hourly page {}", response.getItems().size(), page);
                    hourlyRepositories.addAll(response.getItems());
                    if (response.getItems().size() < 100) {
                        hasMoreData = false;
                    }
                } else {
                    hasMoreData = false;
                }
                page++;
            }

            creationDate = creationDate.plusHours(1);
        }
        log.info("Fetched a total of {} hourly repositories.", hourlyRepositories.size());
        return hourlyRepositories;
    }

    private GitHubRepositoryDTO callGithubRepository(LocalDateTime creationDate, String language, int page)  {
        log.info("Calling GitHub API for language: {}, page: {}, creationDate: {}", language, page, creationDate);
        HttpEntity<Object> entity = buildHttpEntity();
        HashMap<String, String> uriVariables = buildRequestParameters(creationDate, language, page);

        ResponseEntity<GitHubRepositoryDTO> responseEntity = restTemplate.exchange(
                githubUrl + "/search/repositories?q={q}&per_page={per_page}&page={page}",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {},
                uriVariables);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            log.debug("GitHub API call successful for page {}", page);
            return responseEntity.getBody();
        }
        throw new GithubBadRequestException("Failed to get a successful call from Github");
    }

    private GitHubRepositoryDTO callGithubRepository(LocalDateTime creationDate, LocalDateTime now, String language, int page)  {
        log.info("Calling GitHub API for hourly data: {} - {}, page: {}, language: {}", creationDate, now, page, language);
        HttpEntity<Object> entity = buildHttpEntity();
        HashMap<String, String> uriVariables = buildRequestParameters(creationDate, now, language, page);

        ResponseEntity<GitHubRepositoryDTO> responseEntity = restTemplate.exchange(
                githubUrl + "/search/repositories?q={q}&per_page={per_page}&page={page}",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {},
                uriVariables);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            log.debug("GitHub API call successful for hourly data on page {}", page);
            return responseEntity.getBody();
        }
        throw new GithubBadRequestException("Failed to get a successful call from Github");
    }

    private static HashMap<String, String> buildRequestParameters(LocalDateTime creationDate, String language, int page) {
        HashMap<String, String> uriVariables = new HashMap<>();
        //When used with > fetches all repositories greater than created date
        uriVariables.put("q", "created:>" + creationDate + "+language:" + language);
        uriVariables.put("per_page", String.valueOf(MAX_RESULT_LIMIT_PER_PAGE));
        uriVariables.put("page", String.valueOf(page));
        return uriVariables;
    }

    private static HashMap<String, String> buildRequestParameters(LocalDateTime creationDate, LocalDateTime now, String language, int page) {
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("q", "created:" + creationDate + ".."+ now +"+language:" + language);
        uriVariables.put("per_page", String.valueOf(MAX_RESULT_LIMIT_PER_PAGE));
        uriVariables.put("page", String.valueOf(page));
        return uriVariables;
    }

    private HttpEntity<Object> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", GITHUB_APPLICATION_VND_GITHUB_TEXT_MATCH_JSON_HEADER);
        headers.set("Authorization", "Bearer " + githubToken);
        return new HttpEntity<>(headers);
    }
}
