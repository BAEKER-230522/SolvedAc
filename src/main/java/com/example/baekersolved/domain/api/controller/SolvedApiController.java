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
     * @param baekJoonName
     */
    @GetMapping("/v1/valid/{name}")
    public RsData<BaekJoonDto> checkBaekJoonName(@PathVariable("name") String baekJoonName)  {

        try {
            if (solvedApiService.isUser(baekJoonName)) {
                return RsData.successOf(solvedApiService.getJoinSolved(baekJoonName));
            }
        }catch (IOException | ParseException e){
            throw new NotFoundException(NOT_FOUND_USER.getMsg());
        }
        throw new NotFoundException(NOT_FOUND_USER.getMsg());
    }

    @GetMapping("/v1/{problemId}")
    public RsData<SolvedResponse> getSolvedSubject(@PathVariable Long problemId){
        String solvedSubject = null;
        try {
            solvedSubject = solvedApiService.getSolvedSubject(problemId);
        } catch (HttpResponseException e) {
            log.error("solved ac 접속 에러");
            e.printStackTrace();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return RsData.successOf(new SolvedResponse(solvedSubject));
    }

}
