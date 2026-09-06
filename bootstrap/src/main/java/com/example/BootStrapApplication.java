package com.example;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
@EntityScan(basePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
@SpringBootApplication(scanBasePackages = "com.example")
@EnableSpringDataWebSupport
public class BootStrapApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootStrapApplication.class, args);
	}

	// 테스트에서 spring.scheduling.enabled=false로 끄지 않으면, 통합 테스트 종료 시
	// Testcontainers가 먼저 내려간 뒤에도 스케줄러가 DB/Redis에 접근을 시도해
	// 커넥션 타임아웃(각 30초)만큼 컨텍스트 종료가 지연된다.
	@Configuration
	@ConditionalOnProperty(prefix = "spring.scheduling", name = "enabled", matchIfMissing = true)
	@EnableScheduling
	static class SchedulingConfig {
	}
}
