package com.assessment.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class RenderLogsApplication {
	private static final Logger logger = LoggerFactory.getLogger(RenderLogsApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(RenderLogsApplication.class, args);
		logger.info("Configurations loaded");
	}
}
