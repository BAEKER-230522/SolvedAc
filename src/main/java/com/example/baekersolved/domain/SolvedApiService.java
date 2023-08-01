package com.example.baekersolved.domain;

import com.example.baekersolved.domain.api.feign.Feign;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.model.SolvedApiManager;
import com.example.baekersolved.exception.NotFoundException;
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

import static com.example.baekersolved.constants.ExceptionMsg.NOT_FOUND_STUDY;
import static com.example.baekersolved.constants.ExceptionMsg.NOT_FOUND_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
    private final Feign feign;

    /**
     * 난이도별 체크 후 문제풀이 수 리턴
     */
    private Integer getSolvedCount(String baekJoonName, Integer min, Integer max) throws IOException, ParseException {
        JSONArray test;
        try {
            test = this.solvedApiManager.getProblemCount(baekJoonName);

        } catch (HttpClientErrorException e) {
            throw new NotFoundException(NOT_FOUND_USER.getMsg());
        }

        Long solvedCount = 0L;
        for (Object o : test) {
            JSONObject jsonObject = (JSONObject) o;
            if ((Long) jsonObject.get("level") >= min && (Long) jsonObject.get("level") < max) {
                solvedCount += (Long) jsonObject.get("solved");
            }
        }
        return solvedCount.intValue();
    }
    /**
     * Batch Logic
     */
    public RsData<BaekJoonDto> batchLogic(MemberDto memberDto) throws IOException, ParseException, NotFoundException {
        int Bronze = getSolvedCount(memberDto.getBaekJoonName(), 1, 6) - memberDto.getBronze();

        int Silver = getSolvedCount(memberDto.getBaekJoonName(), 6, 11) - memberDto.getSilver();

        int Gold = getSolvedCount(memberDto.getBaekJoonName(), 11, 16) - memberDto.getGold();

        int Platinum = getSolvedCount(memberDto.getBaekJoonName(), 16, 21) - memberDto.getPlatinum();

        int Diamond = getSolvedCount(memberDto.getBaekJoonName(), 21, 26) - memberDto.getDiamond();

        int Ruby = getSolvedCount(memberDto.getBaekJoonName(), 26, 31) - memberDto.getRuby();

        BaekJoonDto dto = new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
        return RsData.successOf(dto);
    }


    /**
     * 최초 로그인 시 업데이트
     */
    public BaekJoonDto getJoinSolved(String baekJoonName) throws IOException, ParseException, NotFoundException {
        int Bronze = getSolvedCount(baekJoonName, 1, 6);
        int Silver = getSolvedCount(baekJoonName, 6, 11);

        int Gold = getSolvedCount(baekJoonName, 11, 16);

        int Platinum = getSolvedCount(baekJoonName, 16, 21);

        int Diamond = getSolvedCount(baekJoonName, 21, 26);

        int Ruby = getSolvedCount(baekJoonName, 26, 31);
        return new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
    }



    /**
     * 회원가입시 사용자 체크
     */
    public boolean isUser(String baekjoonName) throws IOException, ParseException {
        try {
            solvedApiManager.findUser(baekjoonName);
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
            throw new NotFoundException(NOT_FOUND_STUDY.getMsg());
        }
        return studyRule.getData();
    }

    public String getSolvedSubject(int problemId) throws Exception{
        return solvedApiManager.getSubject(problemId);
    }

//    public List<StudyRuleDto> getStudyRuleDtoList() {
//        String jsonStr = restTemplate.getForObject(STUDYRULE_URL, String.class);
//    }

}
