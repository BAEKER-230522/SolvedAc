package com.example.baekersolved.domain;

import com.example.baekersolved.exception.HttpResponseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
@Getter
@RequiredArgsConstructor
public class SolvedApiManager {
    private final String BASE_URL = "https://solved.ac/api/v3/user";
    private final String api_user = "/show";
    private final String api_problem = "/problem_stats";
    private final String api_handle = "?handle=";
    private final RestTemplate restTemplate;

    //== 요청 정보 == //
    private String getUserInformation(String baekJoonName) throws UnsupportedEncodingException {
        return BASE_URL +
                api_user +
                api_handle +
                baekJoonName;
    }

    private String getProblemStats(String baekJoonName) throws UnsupportedEncodingException{
        return BASE_URL +
                api_problem +
                api_handle +
                baekJoonName;
    }



    /**
     * 문제풀이 로직
     */
    public JSONArray getProblemCount(String baekJoonName) throws IOException, ParseException {
        String jsonString = restTemplate.getForObject(getProblemStats(baekJoonName), String.class);
        JSONParser jsonParser = new JSONParser();
        Object jsonObject = jsonParser.parse(jsonString);

        return (JSONArray) jsonObject;
    }

    /**
     * 사용자 정보
     */
    public String findUser(String baekJoonName) throws IOException, ParseException {
        String jsonString = restTemplate.getForObject(getUserInformation(baekJoonName), String.class);

        JSONParser jsonParser = new JSONParser();
        Object jsonObject = jsonParser.parse(jsonString);

        JSONObject jsonBody = (JSONObject) jsonObject;

        return jsonBody.get("handle").toString();
    }

    /**
     * 문제 정보
     * param : 문제 번호
     * return : 문제 제목
     */
    public String getSubject(int problemId) throws Exception{
        String jsonString = null;

        try {
            jsonString = restTemplate.getForObject("https://solved.ac/api/v3/problem/show?problemId=" + problemId, String.class);
        }catch (Exception e){
            e.printStackTrace();
            throw new HttpResponseException("문제 정보를 가져오는데 실패했습니다.");
        }

        JSONParser jsonParser = new JSONParser();
        Object jsonObject = null;
        try {
            jsonObject = jsonParser.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = (JSONObject) jsonObject;

        return jsonBody.get("titleKo").toString();
    }
}
