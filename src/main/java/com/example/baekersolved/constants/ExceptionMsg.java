package com.example.baekersolved.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionMsg {
    NOT_FOUND_USER("해당 유저가 존재하지 않습니다."),
    NOT_FOUND_STUDY("해당 스터디가 존재하지 않습니다.");
    private final String msg;
}
