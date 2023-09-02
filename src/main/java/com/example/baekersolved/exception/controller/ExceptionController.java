package com.example.baekersolved.exception.controller;

import com.example.baekersolved.exception.exception.CrawlingException;
import com.example.baekersolved.exception.ErrorMsg;
import com.example.baekersolved.exception.exception.HttpResponseException;
import com.example.baekersolved.exception.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.baekersolved.exception.ErrorResponse.*;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMsg> notFoundExceptionHandler(NotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(NOT_FOUND_ERROR.getStatus())
                .body(new ErrorMsg(e.getMessage()));
    }

    @ExceptionHandler(HttpResponseException.class)
    public ResponseEntity<ErrorMsg> httpResponseExceptionHandler(HttpResponseException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HTTP_RESPONSE_ERROR.getStatus())
                .body(new ErrorMsg(e.getMessage()));
    }

    @ExceptionHandler(CrawlingException.class)
    public ResponseEntity<ErrorMsg> crawlingExceptionHandler(CrawlingException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(CRAWLING_ERROR.getStatus())
                .body(new ErrorMsg(e.getMessage()));
    }
}
