package com.partygo.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.config.WxConfig;
import com.partygo.model.PartyAbs;
import com.partygo.service.PartyAbsService;
import com.partygo.service.PgStatisService;
import com.partygo.service.RedisService;
import com.partygo.util.LogUtil;
import com.partygo.util.UuidUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PartyAbsController {
	
	@Resource
	private PartyAbsService partyAbsService;
	@Resource
	private PgStatisService pgStatisService;
	@Resource
	private WxConfig wxConfig;
	@Resource
	private RedisService redisService;
	
	@ApiOperation(value="获取聚会摘要信息", notes="根据聚会id获取聚会摘要信息")
	@RequestMapping(value="/partyabs.json",method=RequestMethod.GET)
	public JsonResult getAbsByPid(@RequestParam  String pid) {
		LogUtil.info("执行getAbsByPid,pid="+pid);
		JsonResult res = new JsonResult();
		try {
			PartyAbs abs =  partyAbsService.getPartyAbsById(pid);
			if(abs == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
				LogUtil.error(new Exception("partyAbsService.getPartyAbsById返回为空"), getClass());
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(abs);
				LogUtil.info("查询成功");
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		pgStatisService.statisCall("partyabs", res.getCode(), res.getMessage());
		LogUtil.info("getAbsByPid执行结束");
		return res;
		
	}
	
	
	@ApiOperation(value="获取聚会摘要信息List", notes="根据openid获取聚会摘要信息List")
	@RequestMapping(value="/partyabsList.json/{openid}",method=RequestMethod.GET)
	public JsonResult getAbsListByOpenid(@PathVariable String openid) {
		LogUtil.info("开始执行getAbsListByPid，openid:"+openid);
		JsonResult res = new JsonResult();
		try {
			if(!redisService.exists(openid)) {
				res.setCode("0003");
				res.setMessage("未获取到登录状态");
				LogUtil.error(new Exception("partyAbsService.getPartyAbsListByOpenId未获取到登录状态"), getClass());
				pgStatisService.statisCall("partyabsList", res.getCode(), res.getMessage());
				return res;
			}
			String realOpenid = redisService.hashGet(openid, "openid").toString();
			List<PartyAbs> abs =  partyAbsService.getPartyAbsListByOpenId(realOpenid);
			if(abs == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
				LogUtil.error(new Exception("partyAbsService.getPartyAbsListByOpenId返回为空"), getClass());
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(abs);
				LogUtil.info("查询成功");
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		LogUtil.info("getAbsListByPid执行结束");
		pgStatisService.statisCall("partyabsList", res.getCode(), res.getMessage());
		return res;
		
	}
	
	@ApiOperation(value="添加聚会摘要信息", notes="添加聚会摘要信息")
	@RequestMapping(value="/addPartyAbs.json",method=RequestMethod.POST)
	public JsonResult addAbs(@RequestBody PartyAbs pa) {
		LogUtil.info("执行addAbs");
		JsonResult res = new JsonResult();
		try {
			if(pa == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				pa.setCreateTime(nousedate);
				pa.setUpdateTime(nousedate);
				pa.setAppid(wxConfig.getAppid());
				pa.setPartyid(UuidUtil.getIdByUUId());
				Integer count = partyAbsService.addPartyAbs(pa);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("添加聚会摘要数据异常，count = null");
					LogUtil.error(new Exception("添加聚会摘要数据异常，count = null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("添加聚会摘要数据成功");
						LogUtil.info("添加聚会摘要数据成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("添加聚会摘要失败");
						LogUtil.error(new Exception("添加聚会摘要数据失败，count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("addAbs执行结束");
		pgStatisService.statisCall("addPartyAbs", res.getCode(), res.getMessage());
		return res;
	}
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index() {
		return "hi~partygo项目就要上线了，你还在等什么~";
	}
	

}
