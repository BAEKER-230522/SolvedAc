package com.example.baekersolved.in;

import com.example.baekersolved.domain.SolvedApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SolvedApiController {
    private final SolvedApiService solvedApiService;

    @GetMapping("/api/solved/v1/valid")
    public String checkBaekJoonName(@RequestBody String baekJoonName) throws IOException, ParseException {
        if (solvedApiService.isUser(baekJoonName)) {
            return "S-1";
        }
        return "F-1";
    }
}
