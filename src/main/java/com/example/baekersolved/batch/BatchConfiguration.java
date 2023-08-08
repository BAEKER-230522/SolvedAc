package com.example.baekersolved.batch;

import com.example.baekersolved.constants.Address;
import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.request.RecentUpdateDto;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.StudyRuleProduceDto;
import com.example.baekersolved.domain.dto.response.UserRecentProblem;
import com.example.baekersolved.exception.exception.NotFoundException;
import com.example.baekersolved.global.config.RestTemplateConfig;
import com.example.baekersolved.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.baekersolved.constants.Address.MEMBER_LASTSOLVEDID_UPDATE;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfiguration {

    private final SolvedApiService solvedApiService;
    private final KafkaProducer producer;
    private final PlatformTransactionManager transactionManager;
    private final RestTemplateConfig restTemplate;
    @Value("${custom.server}")
    private String GATEWAY_URL;

    @Bean
    public Job solvedJob(JobRepository jobRepository) {
        return new JobBuilder("solved", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(solvedStep(jobRepository))
                .build();
    }
    @Bean
    @JobScope
    public Step solvedStep(JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager).build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            List<MemberDto> memberList = solvedApiService.getMemberDtoList();
            for (MemberDto member : memberList) {
                try {
                    Thread.sleep(1000);
                    System.out.println(member.getBaekJoonName());
                    BaekJoonDto dto = solvedApiService.batchLogic(member).getData(); // 오늘값 - 어제값 풀이 수

                    MemberDto updateDto = new MemberDto(member, dto); // 새로운 memberDto
                    producer.sendMember(updateDto);
                } catch (NullPointerException | HttpClientErrorException | InterruptedException | NotFoundException e) {
                    log.error("###############" + e.getMessage() + "###############");
                }
            }
            return RepeatStatus.FINISHED;
        } );
    }


    @Bean
    public Job studyJob(JobRepository repository) {
        return new JobBuilder("StudyJob", repository)
                .incrementer(new RunIdIncrementer())
                .start(studyStep(repository))
                .build();
    }

    @Bean
    @JobScope
    public Step studyStep(JobRepository repository) {
        return new StepBuilder("StudyStep", repository)
                .tasklet(studyTasklet(), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet studyTasklet() {
        return (contribution, chunkContext) -> {
            List<StudyRuleConsumeDto> dtoList = solvedApiService.getStudyRule();
            for (StudyRuleConsumeDto dto : dtoList) {
                Long studyRuleId = dto.id();
                producer.sendStudy(new StudyRuleProduceDto(studyRuleId));
                // studyRule ->
            }
            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public Job lastSolvedJob(JobRepository jobRepository) {
        return new JobBuilder("solved", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(lastCrawling(jobRepository))
                .build();
    }

    @JobScope
    @Bean
    public Step lastCrawling(JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(lastSolvedCrawling(), transactionManager).build();
    }

    @StepScope
    @Bean
    public Tasklet lastSolvedCrawling() {
        return ((contribution, chunkContext) -> {
            List<MemberDto> memberList = solvedApiService.getMemberDtoList();
            for (MemberDto member : memberList) {
                try {
                    Thread.sleep(1000);
                    int lastSolvedProblemId = member.getLastSolvedProblemId();
                    String baekJoonName = member.getBaekJoonName();
                    UserRecentProblem userRecentProblem = solvedApiService.recentSolvingProblem(baekJoonName, lastSolvedProblemId);
//                    producer.sendRecentProblem(userRecentProblem); TODO: kafka 작업 후 kafka 로 변경
                    RestTemplate restTemplate = restTemplate();
                    int recentProblemId = Integer.parseInt(userRecentProblem.recentProblemId());
                    RecentUpdateDto dto = new RecentUpdateDto(member.getId(), recentProblemId);
                    restTemplate.postForObject(GATEWAY_URL + MEMBER_LASTSOLVEDID_UPDATE, dto, Void.class);
                } catch (NullPointerException | HttpClientErrorException | InterruptedException | NotFoundException e) {
                    log.error("###############" + e.getMessage() + "###############");
                }
            }
            return RepeatStatus.FINISHED;
        });
    }

    private RestTemplate restTemplate() {
        return restTemplate.restTemplate();
    }
}
