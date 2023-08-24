package com.example.baekersolved.domain;

import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.UserRecentProblem;
import com.example.baekersolved.domain.model.SolvedApiManager;
import com.example.baekersolved.domain.model.SolvedCrawling;
import com.example.baekersolved.exception.ErrorStatus;
import com.example.baekersolved.exception.exception.CrawlingException;
import com.example.baekersolved.exception.exception.NotFoundException;
import com.example.baekersolved.global.config.RestTemplateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.baekersolved.constants.Address.MEMBER_ALL;
import static com.example.baekersolved.constants.Address.STUDYRULE_ALL;
import static com.example.baekersolved.exception.ErrorStatus.NOT_FOUND_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Qualifier("customerHttpClient")
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
//    private final Feign feign;
    private final SolvedCrawling crawling;
    private final RestTemplateConfig restTemplate;
    @Value("${custom.server}")
    public String GATEWAY_URL;

//    @Value("${custom.port}")
//    private String PORT;

    /**
     * 난이도별 체크 후 문제풀이 수 리턴
     */
    private Integer getSolvedCount(String baekJoonName, Integer min, Integer max) throws IOException, ParseException {
        JSONArray test;
        try {
            test = this.solvedApiManager.getProblemCount(baekJoonName);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
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
//    @Deprecated

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
     * member 정보 가져오기
     * TODO:에러 체크 확인 필요
     */
    public List<MemberDto> getMemberDtoList() throws ParseException {
        List<MemberDto> list = new ArrayList<>();
        String response = restTemplate().getForObject(GATEWAY_URL /*+ PORT*/ + MEMBER_ALL, String.class);

        JSONParser parser = new JSONParser();

        JSONObject jsonObject = (JSONObject) parser.parse(response);

        JSONArray jsonArray = (JSONArray) parser.parse(jsonObject.get("data").toString());
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            MemberDto dto = new MemberDto(object.get("id"), object.get("baekJoonName"), object.get("bronze"),object.get("silver"),  object.get("gold"),object.get("platinum"),object.get("diamond"), object.get("ruby"), object.get("lastSolvedProblemId"));
            list.add(dto);
        }
        return list;
    }


    /**
     * StudyRule
     * TODO: 테스트 후 다시 작성
     */
    public List<StudyRuleConsumeDto> getStudyRule() throws ParseException {
        List<StudyRuleConsumeDto> list = new ArrayList<>();
        String response = restTemplate().getForObject(GATEWAY_URL /*+ PORT*/ + STUDYRULE_ALL, String.class);

        JSONParser parser = new JSONParser();

        JSONObject jsonObject = (JSONObject) parser.parse(response);
        JSONArray jsonArray = (JSONArray) parser.parse(jsonObject.get("data").toString());
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            StudyRuleConsumeDto dto = new StudyRuleConsumeDto(Long.parseLong(object.get("id").toString()), object.get("name").toString(),object.get("about").toString() ,Long.parseLong(object.get("ruleId").toString()));
            list.add(dto);
        }
        return list;
    }

//    @Deprecated
    public String getSolvedSubject(int problemId) throws Exception{
        return solvedApiManager.getSubject(problemId);
    }

    /**
     * 백준 프로필 크롤링
     */
    public BaekJoonDto userProfile(String baekjoonId) {
        try {
            return crawling.profileCrawling(baekjoonId);
        } catch (Exception e) {
            throw new CrawlingException(e.getMessage());
        }
    }

    /**
     * 최근 푼 문제 크롤링
     */
    public UserRecentProblem recentSolvingProblem(Long memberId,String baekjoonId, int lastSolvedId) {
        try {
            return crawling.missionSolvedCheck(baekjoonId, lastSolvedId);
        } catch (IOException | InterruptedException e) {
            throw new CrawlingException(ErrorStatus.CRAWLING_ERROR.getMsg());
        }
    }
//    public List<StudyRuleDto> getStudyRuleDtoList() {
//        String jsonStr = restTemplate.getForObject(STUDYRULE_URL, String.class);
//    }

    private RestTemplate restTemplate() {
        return restTemplate.restTemplate();
    }
}
