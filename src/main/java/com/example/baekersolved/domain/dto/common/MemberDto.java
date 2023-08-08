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
    int lastSolvedProblemId;

    public MemberDto(Long id, String baekJoonName, int bronze, int silver, int gold, int platinum, int diamond, int ruby, int lastSolvedProblemId) {
        this.id = id;
        this.baekJoonName = baekJoonName;
        this.bronze = bronze;
        this.silver = silver;
        this.gold = gold;
        this.platinum = platinum;
        this.diamond = diamond;
        this.ruby = ruby;
        this.solvedCount = bronze + silver + gold + platinum + diamond + ruby;
        this.lastSolvedProblemId = lastSolvedProblemId;
    }

    public MemberDto(Object id, Object baekJoonName, Object bronze,Object silver, Object gold, Object platinum, Object diamond, Object ruby, Object lastSolvedProblemId) {
        this.id = (Long) id;
        this.baekJoonName = (String) baekJoonName;
        this.bronze = Integer.parseInt(bronze.toString());
        this.silver = Integer.parseInt(silver.toString());
        this.gold = Integer.parseInt(gold.toString());
        this.platinum = Integer.parseInt(platinum.toString());
        this.diamond = Integer.parseInt(diamond.toString());
        this.ruby = Integer.parseInt(ruby.toString());
        this.solvedCount = this.bronze + this.silver + this.gold + this.platinum + this.diamond + this.ruby;
        try {
            this.lastSolvedProblemId = Integer.parseInt(lastSolvedProblemId.toString());
        } catch (NullPointerException e) {
            this.lastSolvedProblemId = 0;
        }
    }

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
