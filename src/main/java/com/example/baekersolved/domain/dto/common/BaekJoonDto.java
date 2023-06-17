package com.example.baekersolved.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaekJoonDto {
    private final int bronze;
    private final int silver;
    private final int gold;
    private final int platinum;
    private final int diamond;
    private final int ruby;
}
