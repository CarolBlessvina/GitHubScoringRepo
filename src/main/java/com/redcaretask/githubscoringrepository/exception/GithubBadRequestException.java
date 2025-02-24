package com.redcaretask.githubscoringrepository.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GithubBadRequestException extends RuntimeException {
    public GithubBadRequestException(String errorMessage){
        super(errorMessage);
    }
}
