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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PartyAbsController {
	
	@Resource
	private PartyAbsService partyAbsService;
	
	@ApiOperation(value="获取聚会摘要信息", notes="根据聚会id获取聚会摘要信息")
	@ApiImplicitParam(name = "pid", value = "聚会ID", required = true, dataType = "String", paramType = "path")
	@RequestMapping(value="/partyabs.json/{pid}",method=RequestMethod.GET)
	public JsonResult getAbsByPid(@PathVariable String pid) {
		JsonResult res = new JsonResult();
		try {
			PartyAbs abs =  partyAbsService.getPartyAbsById(pid);
			if(abs == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(abs);
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
		}
		
		return res;
		
	}
	
	
	@ApiOperation(value="获取聚会摘要信息List", notes="根据聚会id获取聚会摘要信息List")
	@ApiImplicitParam(name = "pid", value = "聚会ID", required = true, dataType = "String", paramType = "path")
	@RequestMapping(value="/partyabsList.json/{pid}",method=RequestMethod.GET)
	public JsonResult getAbsListByPid(@PathVariable String pid) {
		JsonResult res = new JsonResult();
		try {
			List<PartyAbs> abs =  partyAbsService.getPartyAbsListById(pid);
			if(abs == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(abs);
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
		}
		
		return res;
		
	}
	
	@ApiOperation(value="添加聚会摘要信息", notes="添加聚会摘要信息")
	@RequestMapping(value="/AddPartyAbs.json",method=RequestMethod.POST)
	public JsonResult addAbs(@RequestBody PartyAbs pa) {
		JsonResult res = new JsonResult();
		try {
			if(pa == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				pa.setCreateTime(nousedate);
				pa.setUpdateTime(nousedate);
				int count = partyAbsService.addPartyAbs(pa);
				if(count == 1) {
					res.setCode("0000");
					res.setMessage("添加聚会摘要数据成功");
				}
				else {
					res.setCode("0003");
					res.setMessage("添加聚会摘要失败");
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
		}
		return res;
	}
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index() {
		return "hi";
	}
	

}
