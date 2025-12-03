package com.bj.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
	
	 	@Value("${redis.host}")
	    private String host;

	    @Value("${redis.port}")
	    private int port;

	    @Value("${redis.username}")
	    private String username;

	    @Value("${redis.password}")
	    private String password;

	    @Value("${redis.timeout:2000}")
	    private int timeout;
	   
    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(false); 
        return new JedisPool(
                config,
                host,
                port,
                timeout,    
                username,
                password
        );
    }
}
