package com.example.baekersolved.domain.dto;

/**
 *
 * @param solvedId = 제출번호
 * @param problemId = 문제 번호
 */
public record RecentProblemDto(String solvedId, String problemId, String memory, String time) {
}
