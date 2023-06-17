package com.example.baekersolved.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    Long id;
    String baekJoonName;
    int bronze;
    int silver;
    int gold;
    int platinum;
    int diamond;
    int ruby;
    int solvedCount;


    public MemberDto(MemberDto memberDto, BaekJoonDto baekJoonDto) {
        this.id = memberDto.getId();
        this.baekJoonName = memberDto.getBaekJoonName();
        this.bronze = baekJoonDto.getBronze();
        this.silver = baekJoonDto.getSilver();
        this.gold = baekJoonDto.getGold();
        this.platinum = baekJoonDto.getPlatinum();
        this.diamond = baekJoonDto.getDiamond();
        this.ruby = baekJoonDto.getRuby();
        this.solvedCount = bronze + silver + gold + platinum + diamond + ruby;
    }
}
