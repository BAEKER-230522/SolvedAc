package com.example.baekersolved.domain.dto;

import java.util.List;

public record StudyRuleStatusUpdateDto(Long memberId, List<ProblemNumberDto> problemNumberDtos) {
}
