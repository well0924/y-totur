package com.example.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(value = "com.example.jpa")
@EnableJpaRepositories(basePackages = "com.example.outconnector.repository")
public class JpaConfig {
}
