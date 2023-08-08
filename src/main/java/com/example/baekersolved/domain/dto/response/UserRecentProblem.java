package com.example.baekersolved.domain.dto.response;

import com.example.baekersolved.domain.dto.RecentProblemDto;

import java.util.List;

public record UserRecentProblem(List<RecentProblemDto> recentProblemDtos, String recentProblemId) {
}
