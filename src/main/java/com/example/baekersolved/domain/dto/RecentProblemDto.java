package com.example.baekersolved.domain.dto;

import java.util.List;

/**
 *
 * @param solvedId = 제출번호
 * @param problemId = 문제 번호
 */
public record RecentProblemDto(String solvedId, String problemId) {
}
