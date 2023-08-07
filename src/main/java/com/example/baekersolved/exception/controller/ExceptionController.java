package com.example.baekersolved.exception.controller;

import com.example.baekersolved.exception.ErrorStatus;
import com.example.baekersolved.exception.exception.CrawlingException;
import com.example.baekersolved.exception.ErrorResponse;
import com.example.baekersolved.exception.exception.HttpResponseException;
import com.example.baekersolved.exception.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.baekersolved.exception.ErrorStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(NotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(NOT_FOUND_ERROR.getStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(HttpResponseException.class)
    public ResponseEntity<ErrorResponse> httpResponseExceptionHandler(HttpResponseException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HTTP_RESPONSE_ERROR.getStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(CrawlingException.class)
    public ResponseEntity<ErrorResponse> crawlingExceptionHandler(CrawlingException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(CRAWLING_ERROR.getStatus())
                .body(new ErrorResponse(e.getMessage()));
    }
}
