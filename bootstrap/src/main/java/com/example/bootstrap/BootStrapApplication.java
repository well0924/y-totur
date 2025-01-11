package com.example.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "com.example"})
public class BootStrapApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootStrapApplication.class, args);
	}

}
