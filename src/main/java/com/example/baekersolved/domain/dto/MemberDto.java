package com.example.baekersolved.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String baekJoonName;
    private int bronze;
    private int silver;
    private int gold;
    private int platinum;
    private int diamond;
    private int ruby;

    public MemberDto(MemberDto memberDto, BaekJoonDto baekJoonDto) {
        this.id = memberDto.getId();
        this.baekJoonName = memberDto.getBaekJoonName();
        this.bronze = baekJoonDto.getBronze();
        this.silver = baekJoonDto.getSilver();
        this.gold = baekJoonDto.getGold();
        this.platinum = baekJoonDto.getPlatinum();
        this.diamond = baekJoonDto.getDiamond();
        this.ruby = baekJoonDto.getRuby();
    }
}
