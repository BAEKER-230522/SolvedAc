package com.example.baekersolved.domain;

import com.example.baekersolved.domain.dto.BaekJoonDto;
import com.example.baekersolved.domain.dto.MemberDto;
import com.example.baekersolved.domain.dto.RsData;
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
import java.util.Optional;

import static com.example.baekersolved.address.Address.MEMBER_URL;
import static com.example.baekersolved.address.Address.STUDYRULE_URL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolvedApiService {
    private final SolvedApiManager solvedApiManager;
    private final ApplicationEventPublisher publisher;
    private final List<MemberDto> memberDtoList;
    private final List<StudyRuleDto> studyRuleDtoList;

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
     * 수동으로 본인 기록 업데이트
     */
//    public void getSolvedCount(MemberDto member) throws IOException, ParseException {
//
//        int Bronze = getSolvedCount(member, 1, 6) - member.getBronze();
//
//        int Silver = getSolvedCount(member, 6, 11) - member.getSilver();
//
//        int Gold = getSolvedCount(member, 11, 16) - member.getGold();
//
//        int Platinum = getSolvedCount(member, 16, 21) - member.getPlatinum();
//
//        int Diamond = getSolvedCount(member, 21, 26) - member.getDiamond();
//
//        int Ruby = getSolvedCount(member, 26, 31) - member.getRuby();
//
//        BaekJoonDto dto = new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
////        publisher.publishEvent(new BaekJoonEvent(this, member, dto));
//    }

    /**
     * 회원가입시 사용자 체크
     */
    public boolean checkUser(String studyId) throws IOException, ParseException {
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
    public RsData<List<MemberDto>> getMemberDtoList() throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String jsonStr = restTemplate.getForObject(MEMBER_URL, String.class); // Memeber api
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonStr);

        JSONObject jsonObject = (JSONObject) object;
        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
        for (Object o :  jsonArray) {
            JSONObject parseJson = (JSONObject) o;
            MemberDto memberDto = new MemberDto(
                    (Long) parseJson.get("id"), (String) parseJson.get("baekJoonName"),
                    (int) parseJson.get("bronze"), (int) parseJson.get("silver"),
                    (int) parseJson.get("gold"), (int) parseJson.get("platinum"),
                    (int) parseJson.get("diamond"), (int) parseJson.get("ruby"));
            memberDtoList.add(memberDto);
        }
        return RsData.of("S-1","회원데이터", memberDtoList);
    }

//    public List<StudyRuleDto> getStudyRuleDtoList() {
//        String jsonStr = restTemplate.getForObject(STUDYRULE_URL, String.class);
//    }

}
