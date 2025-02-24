package com.redcaretask.githubscoringrepository.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GithubBadRequestException.class)
    public ResponseEntity<String> handleBadRequest(GithubBadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("HTTP Status: " + HttpStatus.BAD_REQUEST.value()
                        + "Message: Bad Request: " + ex.getMessage());
    }
    @ExceptionHandler(GithubServiceUnavailableException.class)
    public ResponseEntity<String> handleGitHubServiceUnavailable(GithubServiceUnavailableException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("HTTP Status: " + HttpStatus.SERVICE_UNAVAILABLE.value()
                        + "Message: Github service unavailable" + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("HTTP Status: " + HttpStatus.INTERNAL_SERVER_ERROR.value()
                        + "Message: An unexpected error occurred.");
    }

}
