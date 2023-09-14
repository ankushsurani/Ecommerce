package com.eworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EworldApplication {

	public static void main(String[] args) {
		SpringApplication.run(EworldApplication.class, args);
	}

}