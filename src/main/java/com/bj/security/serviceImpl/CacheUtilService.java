package com.bj.security.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class CacheUtilService {

	@Autowired
    private JedisPool jedisPool;
    
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void write(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public String read(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
    
    public void saveObject(String key, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
        	String jsonValue = objectMapper.writeValueAsString(value);
            jedis.set(key,jsonValue);
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}

