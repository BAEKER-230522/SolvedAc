package com.example.baekersolved.domain.api.feign;


import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "studyrule", url = "http://${custom.server}:9000")
@Deprecated
public interface Feign {
    /**
     * 기존의 스터디룰조회
     * 얼만큼 풀었는지확인하는것
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/studyrule/v1/search")
    RsData<List<StudyRuleConsumeDto>> getStudyRule();

    @RequestMapping(method = RequestMethod.GET, value = "/api/member/get/v1/all")
    RsData<List<MemberDto>> getMember();
}
