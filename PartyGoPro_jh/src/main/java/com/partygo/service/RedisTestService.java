package com.partygo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service("redisService")
public class RedisTestService {

	@Autowired
    private StringRedisTemplate stringRedisTemplate;
	
	public void setString(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}
	
	public String getString(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}
}
