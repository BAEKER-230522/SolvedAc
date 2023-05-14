package com.example.baekersolved.domain;

import com.example.baekersolved.domain.dto.BaekJoonDto;
import com.example.baekersolved.domain.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
    private final ApplicationEventPublisher publisher;
    private final RestTemplate restTemplate;
    private final List<MemberDto> dtoList;

    /**
     * 난이도별 체크 후 문제풀이 수 리턴
     */
    public Integer getSolvedCount(Long memberId, Integer min, Integer max) throws IOException, ParseException {
        Member member = memberService.getMember(memberId).getData();
        JSONArray test = this.solvedApiManager.getProblemCount(member.getBaekJoonName());
        Long solvedCount = 0L;
        for (Object o : test) {
            JSONObject jsonObject = (JSONObject) o;
            if ((Long)jsonObject.get("level") >= min && (Long) jsonObject.get("level") < max) {
                solvedCount += (Long) jsonObject.get("solved");
            }
        }

        return solvedCount.intValue();
    }

    /**
     * 수동으로 본인 기록 업데이트
     */
    public void getSolvedCount(Long memberId) throws IOException, ParseException {
        Member member = memberService.getMember(memberId).getData();

        int Bronze = getSolvedCount(memberId, 1, 6) - member.getBronze();

        int Silver = getSolvedCount(memberId, 6, 11) - member.getSliver();

        int Gold = getSolvedCount(memberId, 11, 16) - member.getGold();

        int Platinum = getSolvedCount(memberId, 16, 21) - member.getPlatinum();

        int Diamond = getSolvedCount(memberId, 21, 26) - member.getDiamond();

        int Ruby = getSolvedCount(memberId, 26, 31) - member.getRuby();

        BaekJoonDto dto = new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
        publisher.publishEvent(new BaekJoonEvent(this, member, dto));
    }

    /**
     * 회원가입시 사용자 체크
     */
    public boolean findUser(String studyId) throws IOException, ParseException {
        try {
            String check = solvedApiManager.findUser(studyId);
        } catch (HttpClientErrorException e) {
            return false;
        }
        return true;
    }

    /**
     * 유저 값 받아오기
     */
    public List<MemberDto> getDtoList() {
        
    }
}
