package com.example.baekersolved.constants;


public class Address {
    // MicroService 요청
    public static final String MEMBER_BASE_URL = "/api/member";
    public static final String STUDY_BASE_URL = "/api/study";
    public static final String STUDYRULE_BASE_URL = "/api/studyrule";
    public static final String MEMBER_ALL = "/get/v1/all";
    public static final String MEMBER_LASTSOLVEDID_UPDATE = "/v1/last-solved"; // kafka
    public static final String MEMBER_SOLVED_UPDATE = "/solved";
    public static final String STUDYRULE_ALL = "/v1/search";
    public static final String STUDYRULE_UPDATE = "/v1/studyrules/"; // kafka
    public static final String STUDYRULE_UPDATE_END = "/solved"; // kakfa
    public static final String STUDY_UPDATE_URL = "/v1/mission/"; // + memberId
    public static final String STUDY_UPDATE_MEMBER = "/v1/solved";
    public static final String MEMBER_UPDATE_RANKING = "/v1/ranking";
    public static final String STUDY_UPDATE_RANKING = "/v1/ranking";

    // 외부 주소
    public static final String SOLVED_BASE_URL = "https://solved.ac";
    public static final String SOLVED_PROFILE = "/profile/"; // /+id 입력
    public static final String SOLVED_PROBLEM_URL = "/problems/level/"; // /+level 입력 ex) 0~30
    public static final String BAEKJOON_BASE_URL = "https://www.acmicpc.net";
    public static final String BAEKJOON_SOLVED_URL = "/status?user_id="; // /+id 입력
    public static final String BAEKJOON_SOLVED_END = "&result_id=4";
}
