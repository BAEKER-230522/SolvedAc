package com.example.baekersolved.domain.api.controller;

import com.example.baekersolved.constants.ExceptionMsg;
import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.JoinRequest;
import com.example.baekersolved.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.baekersolved.constants.ExceptionMsg.NOT_FOUND_USER;

@RestController
@RequiredArgsConstructor
public class SolvedApiController {
    private final SolvedApiService solvedApiService;


    /**
     * 검증 로직
     * @param baekJoonName
     */
    @GetMapping("/api/solved/v1/valid/{name}")
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

}
