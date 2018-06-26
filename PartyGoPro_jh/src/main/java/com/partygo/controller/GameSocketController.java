package com.partygo.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.StringUtils;
import com.partygo.common.JsonResult;
import com.partygo.service.PgStatisService;
import com.partygo.service.RedisService;
import com.partygo.util.LogUtil;
import com.partygo.util.UuidUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class GameSocketController {
	
	@Resource
	private RedisService redisService;
	
	@Resource
	private PgStatisService pgStatisService;

	@ApiOperation(value="获取当前房间人数", notes="获取当前房间人数")
	@PostMapping(value="/isRoomFull.json")
	public JsonResult isNumFull(@RequestBody String obj) {
		LogUtil.info("执行isNumFull,obj="+obj);
		JsonResult res = new JsonResult();
		try {
			if(obj == null || obj.isEmpty()) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				String shareId = JSON.parseObject(obj).getString("shareid");
				String rid = UuidUtil.generateRoomId(shareId, new Date());
				//判断房间号是否存在
				if(redisService.hashLen(rid) == 0) {
					res.setCode("0003");
					res.setMessage("房间不存在");
					LogUtil.error(new Exception("房间不存在"), getClass());
				}else {
					//判断游戏状态
					Object statusVal = redisService.hashGet(rid, "status");
					String status = statusVal.toString();
					if(StringUtils.isNullOrEmpty(status)) {
						res.setCode("0004");
						res.setMessage("未能获取游戏状态");
						LogUtil.error(new Exception("未能获取游戏状态"), getClass());
					}else {
						if(!status.equals("0")) {
							res.setCode("0005");
							res.setMessage("游戏已经开始无法加入");
							LogUtil.error(new Exception("游戏已经开始无法加入"), getClass());
						}else {
							//判断房间人数状态
							Object numVal = redisService.hashGet(rid, "num");
							Long numLimit = Long.parseLong(numVal.toString());
							Long nowNum = redisService.hashLen(rid) - 1;
							if(numLimit > nowNum) {
								res.setCode("0000");
								res.setMessage("允许加入房间");
							}else {
								res.setCode("0001");
								res.setMessage("房间人数已满，再瘦也挤不进去了~");
							}
						}
					}
				}
			}
		}catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("isNumFull执行结束");
		pgStatisService.statisCall("isNumFull", res.getCode(), res.getMessage());
		return res;
	}
	
	@ApiOperation(value="获取房间号", notes="获取房间号")
	@PostMapping(value="/obtainRoomId.json")
	public JsonResult obtainRoomId(@RequestBody String obj) {
		LogUtil.info("执行isNumFull,obj="+obj);
		JsonResult res = new JsonResult();
		try {
			if(obj == null || obj.isEmpty()) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				String key = JSON.parseObject(obj).getString("key");
				String rid = UuidUtil.generateRoomId(key, new Date());
				res.setCode("0000");
				res.setMessage("成功创建房间号");
				res.setData(rid);
			}
		}catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("obtainRoomId执行结束");
		pgStatisService.statisCall("obtainRoomId", res.getCode(), res.getMessage());
		return res;
	}
	
}
