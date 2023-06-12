package com.example.baekersolved.domain.api.feign;


import com.example.baekersolved.domain.dto.RsData;
import com.example.baekersolved.domain.dto.StudyRuleConsumeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "StudyRule", url = "StudyRuleURL 작성하기")
public interface StudyRuleFeign {
    @RequestMapping(method = RequestMethod.GET, value = "/api/studyrule/v1/search")
    RsData<List<StudyRuleConsumeDto>> getStudyRule();
}
