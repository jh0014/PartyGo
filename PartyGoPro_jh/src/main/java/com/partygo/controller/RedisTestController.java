package com.partygo.controller;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.partygo.common.JsonResult;
import com.partygo.service.RedisTestService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class RedisTestController {

	@Resource
	private RedisTestService redisTestService;
	
	@ApiOperation(value="redis test", notes="redis test")
	@RequestMapping(value="/testRedis.json",method=RequestMethod.POST)
	public JsonResult setString(@RequestBody String obj) {
		JsonResult res = new JsonResult();
		JSONObject json = JSON.parseObject(obj);
		redisTestService.setString(json.getString("key"), json.getString("value"));
		res.setCode("0000");
		res.setMessage("ok");
		res.setData(redisTestService.getString(json.getString("key")));
		return res;
	}
}
