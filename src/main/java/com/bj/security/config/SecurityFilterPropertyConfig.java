package com.bj.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:securityfilter.properties") 
@PropertySource(value = "classpath:securityfilter-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
public class SecurityFilterPropertyConfig {
}
