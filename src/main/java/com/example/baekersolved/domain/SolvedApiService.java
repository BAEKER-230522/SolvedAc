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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.example.baekersolved.address.Address.MEMBER_URL;

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
    public Optional<Integer> getSolvedCount(MemberDto member, Integer min, Integer max) throws IOException, ParseException {
        JSONArray test;
        try {
            test = this.solvedApiManager.getProblemCount(member.getBaekJoonName());

        } catch (HttpClientErrorException e) {
            Integer ex = -1;
            return Optional.of(ex);
        }

        Long solvedCount = 0L;
        for (Object o : test) {
            JSONObject jsonObject = (JSONObject) o;
            if ((Long)jsonObject.get("level") >= min && (Long) jsonObject.get("level") < max) {
                solvedCount += (Long) jsonObject.get("solved");
            }
        }
        return Optional.of(solvedCount.intValue());
    }



    /**
     * 최초 로그인 시 업데이트
     */
    public void getSolvedCount(MemberDto member) throws IOException, ParseException {
        Optional<Integer> Bronze = getSolvedCount(member, 1, 6);
        if (Bronze.get() != -1) {
            int bronze = Bronze.get();
            int Silver = getSolvedCount(member, 6, 11).get();

            int Gold = getSolvedCount(member, 11, 16).get();

            int Platinum = getSolvedCount(member, 16, 21).get();

            int Diamond = getSolvedCount(member, 21, 26).get();

            int Ruby = getSolvedCount(member, 26, 31).get();

            BaekJoonDto dto = new BaekJoonDto(bronze, Silver, Gold, Platinum, Diamond, Ruby);
            MemberDto memberDto = new MemberDto(member, dto);
            kafkaProducer.sendMember(memberDto);
        }
    }

    /**
     * 회원가입시 사용자 체크
     */
    public boolean isUser(String studyId) throws IOException, ParseException {
        try {
            String check = solvedApiManager.findUser(studyId);
        } catch (HttpClientErrorException e) {
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
