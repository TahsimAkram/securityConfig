package com.bj.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.bj.cache", "com.bj.security"})
public class SecurityFilterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityFilterApplication.class, args);
	}
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
