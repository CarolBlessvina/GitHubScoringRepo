package com.redcaretask.githubscoringrepository.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class GithubServiceUnavailableException extends RuntimeException{
    public GithubServiceUnavailableException(String errorMessage){
        super(errorMessage);
    }
}
