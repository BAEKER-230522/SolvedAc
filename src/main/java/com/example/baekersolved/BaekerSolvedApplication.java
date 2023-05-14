package com.example.baekersolved;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BaekerSolvedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaekerSolvedApplication.class, args);
	}

}
