package com.example.baekersolved.global.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@Configuration
@EnableScheduling
@EnableAutoConfiguration
public class BatchConfig {
}
