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
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.model.PartyAbs;
import com.partygo.service.PartyAbsService;
import com.partygo.util.LogUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PartyAbsController {
	
	@Resource
	private PartyAbsService partyAbsService;
	
	@ApiOperation(value="获取聚会摘要信息", notes="根据聚会id获取聚会摘要信息")
	@RequestMapping(value="/partyabs.json/{pid}",method=RequestMethod.GET)
	public JsonResult getAbsByPid(@PathVariable String pid) {
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
		LogUtil.info("getAbsByPid执行结束");
		return res;
		
	}
	
	
	@ApiOperation(value="获取聚会摘要信息List", notes="根据openid获取聚会摘要信息List")
	@RequestMapping(value="/partyabsList.json/{openid}",method=RequestMethod.GET)
	public JsonResult getAbsListByOpenid(@PathVariable String openid) {
		LogUtil.info("开始执行getAbsListByPid，openid:"+openid);
		JsonResult res = new JsonResult();
		try {
			List<PartyAbs> abs =  partyAbsService.getPartyAbsListByOpenId(openid);
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
		return res;
		
	}
	
	@ApiOperation(value="添加聚会摘要信息", notes="添加聚会摘要信息")
	@RequestMapping(value="/AddPartyAbs.json",method=RequestMethod.POST)
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
		return res;
	}
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index() {
		return "hi";
	}
	

}
