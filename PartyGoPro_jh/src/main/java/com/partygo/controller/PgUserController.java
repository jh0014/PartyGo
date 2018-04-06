package com.partygo.controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.model.PgUser;
import com.partygo.service.PgUserService;
import com.partygo.util.LogUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PgUserController {

	@Resource
	private PgUserService pgUserService;
	
	@ApiOperation(value="保存用户信息", notes="保存用户信息")
	@RequestMapping(value="/savePgUser.json",method=RequestMethod.POST)
	public JsonResult savePgUesrInfo(@RequestBody PgUser user) {
		LogUtil.info("执行savePgUesrInfo");
		JsonResult res = new JsonResult();
		try {
			if(user == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("用户信息为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				user.setCreateTime(nousedate);
				user.setUpdateTime(nousedate);
				Integer count = pgUserService.savePgUser(user);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("保存用户信息异常，count = null");
					LogUtil.error(new Exception("pgUserService.savePgUser返回count=null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("保存用户信息成功");
						LogUtil.info("保存用户信息成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("保存用户信息失败");
						LogUtil.error(new Exception("pgUserService.savePgUser返回count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(new Exception(e), getClass());
		}
		LogUtil.info("savePgUesrInfo执行结束");
		return res;
	}
	
	@ApiOperation(value="获取用户数量", notes="获取用户数量")
	@ApiImplicitParam(name = "appid", value = "应用ID", required = true, dataType = "String", paramType = "path")
	@RequestMapping(value="/getCount.json/{appid}",method=RequestMethod.GET)
	public JsonResult getPgUserCount(@PathVariable String appid) {
		LogUtil.info("执行getPgUserCount");
		JsonResult res = new JsonResult();
		try {
			Integer count =  pgUserService.getCount(appid);
			if(count == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
				LogUtil.error(new Exception("pgUserService.getCount返回count=null"), getClass());
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(count);
				LogUtil.info("返回用户数量成功,count="+count);
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		LogUtil.info("getPgUserCount执行结束");
		return res;
		
	}
	
	@ApiOperation(value="更新用户信息", notes="更新用户信息")
	@RequestMapping(value="updatePgUser.json",method=RequestMethod.POST)
	public JsonResult updatePgUserInfo(@RequestBody PgUser user) {
		LogUtil.info("执行updatePgUserInfo");
		JsonResult res = new JsonResult();
		try {
			if(user == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("用户信息为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				user.setCreateTime(nousedate);
				user.setUpdateTime(nousedate);
				Integer count = pgUserService.updatePgUser(user);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("更新用户信息异常，count = null");
					LogUtil.error(new Exception("pgUserService.updatePgUser返回count=null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("更新用户信息成功");
						LogUtil.info("更新用户信息成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("更新用户信息失败");
						LogUtil.error(new Exception("pgUserService.updatePgUser返回count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("updatePgUserInfo执行结束");
		return res;
	}
}
