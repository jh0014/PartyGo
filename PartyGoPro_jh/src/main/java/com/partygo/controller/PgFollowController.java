package com.partygo.controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.model.PgFollow;
import com.partygo.service.PgFollowService;
import com.partygo.service.PgStatisService;
import com.partygo.util.LogUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PgFollowController {

	@Resource
	private PgFollowService pgFollowService;
	@Resource
	private PgStatisService pgStatisService;
	
	@ApiOperation(value="获取关注信息", notes="根据关注信息")
	@RequestMapping(value="/getFollow.json",method=RequestMethod.GET)
	public JsonResult getFollowById(@RequestParam  String openid, @RequestParam String partyid) {
		LogUtil.info("执行getFollowById,openid="+openid+",partyid="+partyid);
		JsonResult res = new JsonResult();
		try {
			PgFollow follow =  pgFollowService.selectById(openid, partyid);
			if(follow == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
				LogUtil.error(new Exception("pgFollowService.selectById返回为空"), getClass());
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(follow);
				LogUtil.info("查询成功");
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		pgStatisService.statisCall("getFollow", res.getCode(), res.getMessage());
		LogUtil.info("getFollowById执行结束");
		return res;
	}
	
	@ApiOperation(value="关注聚会", notes="关注聚会")
	@RequestMapping(value="/savePgFollow.json",method=RequestMethod.POST)
	public JsonResult savePgFollow(@RequestBody PgFollow follow) {
		LogUtil.info("执行savePgFollow");
		JsonResult res = new JsonResult();
		try {
			if(follow == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("关注信息为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				follow.setCreateTime(nousedate);
				follow.setStatus(1);
				Integer count = pgFollowService.savePgFollow(follow);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("保存关注信息异常，count = null");
					LogUtil.error(new Exception("pgFollowService.savePgFollow返回count=null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("保存关注信息成功");
						LogUtil.info("保存关注信息成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("保存关注信息失败");
						LogUtil.error(new Exception("pgFollowService.savePgFollow返回count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(new Exception(e), getClass());
		}
		pgStatisService.statisCall("savePgFollow", res.getCode(), res.getMessage());
		LogUtil.info("savePgFollow执行结束");
		return res;
	}
	
	@ApiOperation(value="取消关注", notes="取消关注")
	@RequestMapping(value="/updatePgFollow.json",method=RequestMethod.POST)
	public JsonResult updatePgFollow(@RequestBody PgFollow follow) {
		LogUtil.info("执行updatePgFollow");
		JsonResult res = new JsonResult();
		try {
			if(follow == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("关注信息为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				follow.setCreateTime(nousedate);
				follow.setStatus(0);
				Integer count = pgFollowService.updatePgFollow(follow);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("更新关注信息异常，count = null");
					LogUtil.error(new Exception("pgFollowService.updatePgFollow返回count=null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("更新关注信息成功");
						LogUtil.info("更新关注信息成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("更新关注信息失败");
						LogUtil.error(new Exception("pgFollowService.updatePgFollow返回count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(new Exception(e), getClass());
		}
		pgStatisService.statisCall("updatePgFollow", res.getCode(), res.getMessage());
		LogUtil.info("updatePgFollow执行结束");
		return res;
	}
}
