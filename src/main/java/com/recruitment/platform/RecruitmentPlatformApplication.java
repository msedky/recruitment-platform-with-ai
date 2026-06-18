package com.recruitment.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RecruitmentPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecruitmentPlatformApplication.class, args);
	}

}
