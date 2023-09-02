package com.example.baekersolved.exception;

import lombok.Getter;

@Getter
public enum ErrorResponse {
    CRAWLING_ERROR(400, "크롤링 에러"),
    HTTP_RESPONSE_ERROR(400, "HTTP 응답 에러"),
    NOT_FOUND_ERROR(404, "찾을 수 없는 리소스"),
    NOT_FOUND_STUDY(404,"해당 스터디가 존재하지 않습니다."),
    NOT_FOUND_USER(404, "해당 유저가 존재하지 않습니다.");
    private final int status;
    private final String msg;

    ErrorResponse(int status, String message) {
        this.status = status;
        this.msg = message;
    }
}
