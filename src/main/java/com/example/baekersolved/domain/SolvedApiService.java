package com.example.baekersolved.domain;

import com.example.baekersolved.domain.dto.BaekJoonDto;
import com.example.baekersolved.domain.dto.MemberDto;
import com.example.baekersolved.domain.dto.StudyRuleDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static com.example.baekersolved.address.Address.MEMBER_URL;
import static com.example.baekersolved.address.Address.STUDYRULE_URL;

@Service
@Transactional
@RequiredArgsConstructor
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
    private final ApplicationEventPublisher publisher;
    private final RestTemplate restTemplate;
    private final List<MemberDto> memberDtoList;
    private final List<StudyRuleDto> studyRuleDtoList;

    /**
     * 난이도별 체크 후 문제풀이 수 리턴
     */
    public Integer getSolvedCount(MemberDto memberDto, Integer min, Integer max) throws IOException, ParseException {
        JSONArray test = this.solvedApiManager.getProblemCount(memberDto.getBaekJoonName());
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
    public void getSolvedCount(MemberDto member) throws IOException, ParseException {

        int Bronze = getSolvedCount(member, 1, 6) - member.getBronze();

        int Silver = getSolvedCount(member, 6, 11) - member.getSilver();

        int Gold = getSolvedCount(member, 11, 16) - member.getGold();

        int Platinum = getSolvedCount(member, 16, 21) - member.getPlatinum();

        int Diamond = getSolvedCount(member, 21, 26) - member.getDiamond();

        int Ruby = getSolvedCount(member, 26, 31) - member.getRuby();

        BaekJoonDto dto = new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
//        publisher.publishEvent(new BaekJoonEvent(this, member, dto));
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
     * member, studyrule 값 받아오기
     */
    public List<MemberDto> getMemberDtoList() throws ParseException {
        String jsonStr = restTemplate.getForObject(MEMBER_URL, String.class); // Memeber api
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonStr);

        for (Object o : (JSONArray) object) {
            JSONObject jsonObject = (JSONObject) o;
            MemberDto memberDto = new MemberDto(
                    (Long) jsonObject.get("id"), (String) jsonObject.get("baekJoonName"),
                    (int) jsonObject.get("bronze"), (int) jsonObject.get("silver"),
                    (int) jsonObject.get("gold"), (int) jsonObject.get("platinum"),
                    (int) jsonObject.get("diamond"), (int) jsonObject.get("ruby"));
            memberDtoList.add(memberDto);
        }
        return memberDtoList;
    }

//    public List<StudyRuleDto> getStudyRuleDtoList() {
//        String jsonStr = restTemplate.getForObject(STUDYRULE_URL, String.class);
//    }

}
