package com.example.baekersolved.batch;

import com.example.baekersolved.BaekerSolvedApplication;
import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.global.config.BatchConfig;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBatchTest
@SpringBootTest(classes = {BatchConfig.class, BatchConfiguration.class, BaekerSolvedApplication.class})
public class BatchJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @MockBean
    SolvedApiService solvedApiService;
    @Autowired
    @Qualifier("solvedJob")
    private Job solvedJob;
    @Autowired
    @Qualifier("studyJob")
    private Job studyJob;
    @Autowired
    @Qualifier("lastSolvedJob")
    private Job lastSolvedJob;

    @BeforeEach
    public void setup() throws ParseException {
        this.jobLauncherTestUtils.setJob(solvedJob);
        this.jobRepositoryTestUtils.removeJobExecutions();

        this.jobLauncherTestUtils.setJob(studyJob);
        this.jobRepositoryTestUtils.removeJobExecutions();

        this.jobLauncherTestUtils.setJob(lastSolvedJob);
        this.jobRepositoryTestUtils.removeJobExecutions();

        List<MemberDto> memberDtos = new ArrayList<>();
        MemberDto memberDto = new MemberDto();
        memberDto.setId(1L);
        memberDtos.add(memberDto);
        when(solvedApiService.getMemberDtoList()).thenReturn(memberDtos);
    }
    @Test
    @DisplayName("Batch Job Test")
    public void simpleJobTest() throws Exception {
        // given
        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
