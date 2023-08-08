package com.example.baekersolved.domain.dto.request;

public record MemberSolvedUpdateDto(Long id, int bronze, int silver, int gold, int diamond, int ruby, int platinum) {
}
