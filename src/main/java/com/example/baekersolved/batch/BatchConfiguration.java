package com.example.baekersolved.batch;

import com.example.baekersolved.domain.SolvedApiService;
import com.example.baekersolved.domain.dto.BaekJoonDto;
import com.example.baekersolved.domain.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.json.simple.parser.ParseException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfiguration {
    private final JobBuilder jobBuilder;

    private final JobRepository jobRepository;
    private final SolvedApiService solvedApiService;

    @Bean
    public ItemReader<MemberDto> read() throws ParseException {
        return new ListItemReader<>(solvedApiService.getMemberDtoList());
    }

    @Bean
    public ItemProcessor<MemberDto, BaekJoonDto> processor() {
        return member -> {
            try {
                int Bronze = solvedApiService.getSolvedCount(member, 1, 6) - member.getBronze();
                Thread.sleep(1000);

                int Silver = solvedApiService.getSolvedCount(member, 6, 11) - member.getSilver();
                Thread.sleep(1000);

                int Gold = solvedApiService.getSolvedCount(member, 11, 16) - member.getGold();
                Thread.sleep(1000);

                int Platinum = solvedApiService.getSolvedCount(member, 16, 21) - member.getPlatinum();
                Thread.sleep(1000);

                int Diamond = solvedApiService.getSolvedCount(member, 21, 26) - member.getDiamond();
                Thread.sleep(1000);

                int Ruby = solvedApiService.getSolvedCount(member, 26, 31) - member.getRuby();
                Thread.sleep(1000);
                System.out.println(Gold);
                return new BaekJoonDto(Bronze, Silver, Gold, Platinum, Diamond, Ruby);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    @Bean
    public ItemWriter<BaekJoonDto> writer() {
        return list -> {
            for (BaekJoonDto dto : list) {
                //publisher.publishEvent(new BaekJoonEvent(this, member, dto));
            }
        };
    }
    @Bean
    public Job testJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("SolvedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end().build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws ParseException {
        return new StepBuilder("step1", jobRepository)
                .<MemberDto, BaekJoonDto> chunk(10, transactionManager)
                .reader(read())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
