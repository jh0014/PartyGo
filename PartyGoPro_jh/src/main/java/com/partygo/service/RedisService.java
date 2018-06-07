package com.partygo.service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service("redisService")
public class RedisService {

	@Autowired
    private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
    private RedisTemplate redisTemplate;
	
	public void setString(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}
	
	public String getString(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}
	
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
            result = true;
		}
		catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
	
	public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
	
	public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
	
	public void hashSet(String key, Object hashKey, Object value, int expireTime, String type){
        try {
			HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
			hash.put(key,hashKey,value);
			if(type.equals("hour")) {
				redisTemplate.expire(key, expireTime, TimeUnit.HOURS);
			}
			else {
				redisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void hashSet(String key, Object hashKey, Object value){
        HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }
	
	public Object hashGet(String key, Object hashKey){
        HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }
	
	public Set<Object> hashKeys(String key){
        HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }
	
	public Object hashDel(String key, Object hashKey){
        HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.delete(key,hashKey);
    }
	
	public Long hashLen(String key){
        HashOperations<Serializable, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.size(key);
    }
	
	public void lPush(String k,Object v){
        ListOperations<Serializable, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }
	
	public List<Object> lRange(String k, long l, long l1){
        ListOperations<Serializable, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }
	
	public void add(String key,Object value){
        SetOperations<Serializable, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }
	
	public Set<Object> setMembers(String key){
        SetOperations<Serializable, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }
	
	public void zAdd(String key,Object value,double scoure){
        ZSetOperations<Serializable, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }
	
	public Set<Object> rangeByScore(String key,double scoure,double scoure1){
        ZSetOperations<Serializable, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }
}
