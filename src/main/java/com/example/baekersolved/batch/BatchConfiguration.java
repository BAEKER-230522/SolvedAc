package com.example.baekersolved.batch;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.ProblemNumberDto;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.request.MemberSolvedUpdateDto;
import com.example.baekersolved.domain.dto.request.RecentUpdateDto;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.UserRecentProblem;
import com.example.baekersolved.global.config.RestTemplateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.baekersolved.constants.Address.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfiguration {

    private final SolvedApiService solvedApiService;
//    private final KafkaProducer producer; TODO: kafka 작업 후 kafka 로 변경
    private final PlatformTransactionManager transactionManager;
    private final RestTemplateConfig restTemplate;
    @Value("${custom.server}")
    private String GATEWAY_URL;
    @Value("${custom.port}")
    private String PORT;

    @Bean("solvedJob")
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
                    BaekJoonDto dto = solvedApiService.batchLogic(member).getData(); // 오늘값 - 어제값 풀이 수

//                    MemberDto updateDto = new MemberDto(member, dto); // 새로운 memberDto
//                    producer.sendMember(updateDto);
                    MemberSolvedUpdateDto updateDto = new MemberSolvedUpdateDto(member.getId(), dto.getBronze(), dto.getSilver(), dto.getGold(), dto.getDiamond(), dto.getRuby(), dto.getPlatinum());
                    RestTemplate restTemplate = restTemplate();
                    restTemplate.postForObject(GATEWAY_URL + MEMBER_SOLVED_UPDATE, updateDto, Void.class);
                } catch (Exception e) {
                    log.error("###############" + e.getMessage() + "###############");
                }
            }
            return RepeatStatus.FINISHED;
        } );
    }


    @Bean("studyJob")
    @Deprecated
    public Job studyJob(JobRepository repository) {
        return new JobBuilder("studyJob", repository)
                .incrementer(new RunIdIncrementer())
                .start(studyStep(repository))
                .build();
    }

    @Bean
    @JobScope
    @Deprecated
    public Step studyStep(JobRepository repository) {
        return new StepBuilder("studyStep", repository)
                .tasklet(studyTasklet(), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    @Deprecated
    public Tasklet studyTasklet() {
        return (contribution, chunkContext) -> {
            List<StudyRuleConsumeDto> dtoList = solvedApiService.getStudyRule();
            for (StudyRuleConsumeDto dto : dtoList) {
                Long studyRuleId = dto.id();
//                producer.sendStudy(new StudyRuleProduceDto(studyRuleId));
                // studyRule ->
                RestTemplate restTemplate = restTemplate();
                try {
                    restTemplate.getForObject(GATEWAY_URL + STUDYRULE_UPDATE + studyRuleId + STUDYRULE_UPDATE_END, Void.class);
                } catch (HttpServerErrorException e) {
                    log.error(e.getMessage()); // 503 서버 에러
                }

            }
            return RepeatStatus.FINISHED;
        };
    }


    @Bean("lastSolvedJob")
    public Job lastSolvedJob(JobRepository jobRepository) {
        return new JobBuilder("solved", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(lastCrawling(jobRepository))
                .build();
    }

    @JobScope
    @Bean
    public Step lastCrawling(JobRepository jobRepository) {
        return new StepBuilder("lastSolved", jobRepository)
                .tasklet(lastSolvedCrawling(), transactionManager).build();
    }

    @StepScope
    @Bean
    public Tasklet lastSolvedCrawling() {
        return ((contribution, chunkContext) -> {
            log.info("############### lastSolvedCrawling ###############");
            List<MemberDto> memberList = solvedApiService.getMemberDtoList();
            for (MemberDto member : memberList) {
                try {
                    log.info(member.getBaekJoonName() + "데이터 처리중");
                    Thread.sleep(1000);
                    int lastSolvedProblemId = member.getLastSolvedProblemId();
                    String baekJoonName = member.getBaekJoonName();

                    UserRecentProblem userRecentProblem = solvedApiService.recentSolvingProblem(member.getId(),baekJoonName, lastSolvedProblemId);
//                    producer.sendRecentProblem(userRecentProblem); TODO: kafka 작업 후 kafka 로 변경
                    RestTemplate restTemplate = restTemplate();
                    int recentProblemId = Integer.parseInt(userRecentProblem.recentProblemId());
                    RecentUpdateDto dto = new RecentUpdateDto(member.getId(), recentProblemId);
                    restTemplate.postForObject(GATEWAY_URL + MEMBER_LASTSOLVEDID_UPDATE, dto, Void.class);
                    log.info("Member쪽 이상 무" + dto.toString());
                    List<ProblemNumberDto> problemNumberDtos = userRecentProblem.recentProblemDtos().stream()
                            .map(o -> new ProblemNumberDto(o.problemId(), o.time(), o.memory())).toList();

                    restTemplate.postForObject(GATEWAY_URL + STUDY_UPDATE_URL + member.getId(), problemNumberDtos, Void.class);
                    log.info(problemNumberDtos.toString() + "푼문제 잘 품");
                } catch (Exception e) {
                    e.printStackTrace();
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
