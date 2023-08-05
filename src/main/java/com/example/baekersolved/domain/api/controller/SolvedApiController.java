package com.example.baekersolved.domain.api.controller;

import com.example.baekersolved.constants.ExceptionMsg;
import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.JoinRequest;
import com.example.baekersolved.domain.dto.response.SolvedResponse;
import com.example.baekersolved.exception.HttpResponseException;
import com.example.baekersolved.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.baekersolved.constants.ExceptionMsg.NOT_FOUND_USER;

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
     *  api 사용불가로 크롤링으로 수정 예정
     */
    @GetMapping("/v1/{problemId}")
    @Deprecated
    public RsData<SolvedResponse> getSolvedSubject(@PathVariable("problemId") Integer problemId) throws Exception {
        String solvedSubject = solvedApiService.getSolvedSubject(problemId);
        return RsData.successOf(new SolvedResponse(solvedSubject));
    }

    @GetMapping("/v1/{name}/count")
    public RsData<BaekJoonDto> test(@PathVariable("name") String baekJoonName) throws IOException, ParseException {
        BaekJoonDto joinSolved = solvedApiService.getJoinSolved(baekJoonName);
        return RsData.successOf(joinSolved);
    }
}
