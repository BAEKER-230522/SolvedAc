package com.example.baekersolved.constants;


import org.springframework.beans.factory.annotation.Value;

public class Address {
    public static final String MEMBER_ALL = "/api/member/get/v1/all";
    public static final String MEMBER_LASTSOLVEDID_UPDATE = "/api/member/v1/last-solved";
    public static final String MEMBER_SOLVED_UPDATE = "/api/member/solved";
    public static final String STUDYRULE_ALL = "/api/studyrule/v1/search";
    public static final String SOLVED_BASE_URL = "https://solved.ac";
    public static final String SOLVED_PROFILE = "/profile/"; // /+id 입력
    public static final String SOLVED_PROBLEM_URL = "/problems/level/"; // /+level 입력 ex) 0~30
    public static final String BAEKJOON_BASE_URL = "https://www.acmicpc.net";
    public static final String BAEKJOON_SOLVED_URL = "/status?user_id="; // /+id 입력
    public static final String BAEKJOON_SOLVED_END = "&result_id=4";
}
