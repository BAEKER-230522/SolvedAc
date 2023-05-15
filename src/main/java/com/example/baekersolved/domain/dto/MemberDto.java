package com.example.baekersolved.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private final Long id;
    private final String BaekJoonName;
    private final int bronze;
    private final int silver;
    private final int gold;
    private final int platinum;
    private final int diamond;
    private final int ruby;
}
