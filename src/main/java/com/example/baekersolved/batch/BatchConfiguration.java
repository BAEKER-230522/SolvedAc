package com.example.baekersolved.batch;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.StudyRuleProduceDto;
import com.example.baekersolved.exception.NotFoundException;
import com.example.baekersolved.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfiguration {

    private final SolvedApiService solvedApiService;
    private final KafkaProducer producer;
    private final PlatformTransactionManager transactionManager;


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

            RsData<List<MemberDto>> memberList = solvedApiService.getMemberDtoList();
            for (MemberDto member : memberList.getData()) {
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
}
