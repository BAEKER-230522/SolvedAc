package com.example.baekersolved.domain.api.controller;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.SolvedResponse;
import com.example.baekersolved.exception.exception.NotFoundException;
import com.example.baekersolved.global.config.SSLConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.example.baekersolved.exception.ErrorResponse.NOT_FOUND_STUDY;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/solved")
@Slf4j
public class SolvedApiController {
    private final SolvedApiService solvedApiService;


    /**
     * 검증 로직
     *
     * @param baekJoonName
     */
    @GetMapping("/v1/valid/{name}")
    public RsData<BaekJoonDto> checkBaekJoonName(@PathVariable("name") String baekJoonName) {
        //TODO :Exception 처리 디테일 하게
        BaekJoonDto userProfile = solvedApiService.userProfile(baekJoonName);
        return RsData.of("S-1", "성공", userProfile);

    }

    /**
     * api 사용불가로 크롤링으로 수정 예정
     */
    @GetMapping("/v1/{problemId}")
//    @Deprecated
    public RsData<SolvedResponse> getSolvedSubject(@PathVariable("problemId") Integer problemId) throws Exception {
        return RsData.successOf(solvedApiService.getSolvedSubjectAndLevel(problemId));
    }

    @GetMapping("/v1/{name}/count")
    public RsData<BaekJoonDto> test(@PathVariable("name") String baekJoonName) throws IOException, ParseException {
        BaekJoonDto joinSolved = solvedApiService.getJoinSolved(baekJoonName);
        return RsData.successOf(joinSolved);
    }

    /**
     * 외부 api 인 Member 호출 테스트
     * @return
     */
    @GetMapping("/v1/test")
    public RsData<List<StudyRuleConsumeDto>> test() {
        try {
            SSLConfig sslConfig = new SSLConfig();
            sslConfig.disableSslVerification();
            List<StudyRuleConsumeDto> memberDtoList = solvedApiService.getStudyRule();
            return RsData.successOf(memberDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(NOT_FOUND_STUDY.getMsg());
        }
    }

    @GetMapping("/v1/test2")
    public void test2() {
        solvedApiService.recentSolvingProblem(1L, "wy9295",64662791);

    }
}
