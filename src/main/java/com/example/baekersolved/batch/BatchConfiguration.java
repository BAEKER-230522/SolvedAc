package com.example.baekersolved.batch;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.common.BaekJoonDto;
import com.example.baekersolved.domain.dto.common.MemberDto;
import com.example.baekersolved.domain.dto.common.RsData;
import com.example.baekersolved.domain.dto.request.StudyRuleConsumeDto;
import com.example.baekersolved.domain.dto.response.StudyRuleProduceDto;
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
    private final ApplicationEventPublisher publisher;


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
            System.out.println("멤버별 solved count 테스크렛");
            RsData<List<MemberDto>> memberList = solvedApiService.getMemberDtoList();
            for (MemberDto member : memberList.getData()) {
                try {
                    System.out.println(member.getBaekJoonName());
                    Optional<Integer> Bronze = solvedApiService.getSolvedCount(member, 1, 6);
                    if (Bronze.get() == -1) {
                        continue;
                    }
                    int bronze = Bronze.get() - member.getBronze();
                    Thread.sleep(1000);

                    int Silver = solvedApiService.getSolvedCount(member, 6, 11).get() - member.getSilver();
                    Thread.sleep(1000);

                    int Gold = solvedApiService.getSolvedCount(member, 11, 16).get() - member.getGold();
                    Thread.sleep(1000);

                    int Platinum = solvedApiService.getSolvedCount(member, 16, 21).get() - member.getPlatinum();
                    Thread.sleep(1000);

                    int Diamond = solvedApiService.getSolvedCount(member, 21, 26).get() - member.getDiamond();
                    Thread.sleep(1000);

                    int Ruby = solvedApiService.getSolvedCount(member, 26, 31).get() - member.getRuby();
                    Thread.sleep(1000);

                    BaekJoonDto dto = new BaekJoonDto(bronze , Silver, Gold, Platinum, Diamond, Ruby);
                    MemberDto updateDto = new MemberDto(member, dto);
                    producer.sendMember(updateDto);
                } catch (NullPointerException | HttpClientErrorException | InterruptedException e) {
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
