package com.example.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

// @SpringBootTest(classes=...)로 좁게 로드하면 오토컨피규레이션이 돌지 않아
// ObjectMapper/CircuitBreakerRegistry가 자동 생성되지 않는다 -> 직접 빈으로 정의한다.
@TestConfiguration
public class TestSupportConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }
}
