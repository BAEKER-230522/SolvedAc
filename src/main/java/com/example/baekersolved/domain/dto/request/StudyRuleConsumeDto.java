package com.example.baekersolved.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record StudyRuleConsumeDto(

        Long id, //
        String name,
        String about,
        Long ruleId
) {
}