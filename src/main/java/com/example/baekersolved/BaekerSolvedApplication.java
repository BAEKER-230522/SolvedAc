package com.example.baekersolved;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EnableKafka
@EnableDiscoveryClient
public class BaekerSolvedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaekerSolvedApplication.class, args);
	}

}
