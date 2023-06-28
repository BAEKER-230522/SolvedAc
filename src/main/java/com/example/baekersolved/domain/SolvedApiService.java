package com.example.baekersolved.domain;

import com.example.baekersolved.domain.api.feign.Feign;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.exception.NotFoundException;
import com.example.baekersolved.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
    private final KafkaProducer kafkaProducer;
    private final Feign feign;

    /**
     * 난이도별 체크 후 문제풀이 수 리턴
     */
    public Optional<Integer> getSolvedCount(String baekJoonName, Integer min, Integer max) throws IOException, ParseException {
        JSONArray test;
        try {
            test = this.solvedApiManager.getProblemCount(baekJoonName);

        } catch (HttpClientErrorException e) {
            Integer ex = -1;
            return Optional.of(ex);
        }

        Long solvedCount = 0L;
        for (Object o : test) {
            JSONObject jsonObject = (JSONObject) o;
            if ((Long) jsonObject.get("level") >= min && (Long) jsonObject.get("level") < max) {
                solvedCount += (Long) jsonObject.get("solved");
            }
        }
        return Optional.of(solvedCount.intValue());
    }
    /**
     * Batch Logic
     */
    public RsData<BaekJoonDto> batchLogic(MemberDto memberDto) throws IOException, ParseException {
        Optional<Integer> Bronze = getSolvedCount(memberDto.getBaekJoonName(), 1, 6);
        if (Bronze.get() == -1) {
            throw new NotFoundException("해당 유저가 존재하지 않습니다.");
        }
        int bronze = Bronze.get() - memberDto.getBronze();

        int Silver = getSolvedCount(memberDto.getBaekJoonName(), 6, 11).get() - memberDto.getSilver();

        int Gold = getSolvedCount(memberDto.getBaekJoonName(), 11, 16).get() - memberDto.getGold();

        int Platinum = getSolvedCount(memberDto.getBaekJoonName(), 16, 21).get() - memberDto.getPlatinum();

        int Diamond = getSolvedCount(memberDto.getBaekJoonName(), 21, 26).get() - memberDto.getDiamond();

        int Ruby = getSolvedCount(memberDto.getBaekJoonName(), 26, 31).get() - memberDto.getRuby();

        BaekJoonDto dto = new BaekJoonDto(bronze, Silver, Gold, Platinum, Diamond, Ruby);
        return RsData.successOf(dto);
    }


    /**
     * 최초 로그인 시 업데이트
     */
    public BaekJoonDto getJoinSolved(String baekJoonName) throws IOException, ParseException {
        int Bronze = getSolvedCount(baekJoonName, 1, 6).get();
        int Silver = getSolvedCount(baekJoonName, 6, 11).get();

        int Gold = getSolvedCount(baekJoonName, 11, 16).get();

        int Platinum = getSolvedCount(baekJoonName, 16, 21).get();

        int Diamond = getSolvedCount(baekJoonName, 21, 26).get();

        int Ruby = getSolvedCount(baekJoonName, 26, 31).get();
        return new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
    }



    /**
     * 회원가입시 사용자 체크
     */
    public boolean isUser(String baekjoonName) throws IOException, ParseException {
        try {
            String check = solvedApiManager.findUser(baekjoonName);
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * member
     * TODO:에러 체크 확인 필요
     */
    public RsData<List<MemberDto>> getMemberDtoList() {
        return feign.getMember();
    }


    /**
     * StudyRule Feign
     */
    public List<StudyRuleConsumeDto> getStudyRule() {
        RsData<List<StudyRuleConsumeDto>> studyRule = feign.getStudyRule();
        if (studyRule.isFail()) {
            throw new NotFoundException("StudyRule 이 없습니다.");
        }
        return studyRule.getData();
    }

//    public List<StudyRuleDto> getStudyRuleDtoList() {
//        String jsonStr = restTemplate.getForObject(STUDYRULE_URL, String.class);
//    }

}
