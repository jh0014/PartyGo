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
import com.partygo.config.WxConfig;
import com.partygo.model.PartyAbs;
import com.partygo.model.PgDetail;
import com.partygo.service.PartyAbsService;
import com.partygo.service.PartyDetailService;
import com.partygo.service.PartyService;
import com.partygo.service.PgStatisService;
import com.partygo.util.LogUtil;
import com.partygo.util.UuidUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PartyController {

	@Resource
	private PartyAbsService partyAbsService;
	@Resource
	private PartyDetailService partyDetailService;
	@Resource
	private PartyService partyService;
	@Resource
	private PgStatisService pgStatisService;
	@Resource
	private WxConfig wxConfig;
	
	@ApiOperation(value="获取聚会详细信息", notes="根据聚会id获取聚会详细要信息")
	@RequestMapping(value="/partyDetail.json/{pid}",method=RequestMethod.GET)
	public JsonResult getDetail(@PathVariable String pid) {
		LogUtil.info("执行getDetail,pid="+pid);
		JsonResult res = new JsonResult();
		try {
			PgDetail detail =  partyDetailService.selectDetailByPid(pid);
			if(detail == null) {
				res.setCode("0001");
				res.setMessage("返回为空");
				LogUtil.error(new Exception("partyDetailService.selectDetailByPid返回为空"), getClass());
			}
			else {
				res.setCode("0000");
				res.setMessage("返回成功");
				res.setData(detail);
				LogUtil.info("查询成功");
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		pgStatisService.statisCall("partyDetail", res.getCode(), res.getMessage());
		LogUtil.info("getDetail执行结束");
		return res;
	}
	
	//删除
	@ApiOperation(value="删除聚会信息", notes="删除聚会摘要信息/详细信息")
	@RequestMapping(value="/delParty.json",method=RequestMethod.POST)
	public JsonResult delParty(@RequestBody String pid, @RequestBody String openid) {
		LogUtil.info("执行delParty");
		JsonResult res = new JsonResult();
		try {
			if(pid == null || pid.isEmpty()||openid == null || openid.isEmpty()) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				Integer delRes = partyService.deletePartyInfo(pid,openid);
				if(delRes == null || delRes == 0) {
					res.setCode("0003");
					res.setMessage("聚会信息删除失败,partyService.deletePartyInfo返回delRes="+delRes);
					LogUtil.error(new Exception("聚会信息删除失败,partyService.deletePartyInfo返回delRes="+delRes), getClass());
				}
				else {
					res.setCode("0000");
					res.setMessage("聚会信息删除成功");
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("delParty执行结束");
		pgStatisService.statisCall("delParty", res.getCode(), res.getMessage());
		return res;
	}
	
	//发布
	@ApiOperation(value="发布聚会信息", notes="发布聚会摘要信息/详细信息")
	@RequestMapping(value="/publishParty.json",method=RequestMethod.POST)
	public JsonResult publishParty(@RequestBody PgDetail detail) {
		LogUtil.info("执行publishParty");
		JsonResult res = new JsonResult();
		try {
			if(detail == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				String pid = UuidUtil.getIdByUUId();
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				detail.setPartyid(pid);
				detail.setCreateTime(nousedate);
				detail.setUpdateTime(nousedate);
				
				PartyAbs abs = new PartyAbs();
				abs.setAppid(wxConfig.getAppid());
				abs.setPartyid(pid);
				abs.setOpenid(detail.getOpenid());
				abs.setCreateTime(nousedate);
				abs.setPartyTime(detail.getPartyTime());
				abs.setPartyTitle(detail.getPartyTitle());
				abs.setUpperson(detail.getOpenid());
				abs.setStatus(1);
				abs.setUpdateTime(nousedate);
				abs.setIsexpire(0);
				abs.setCreator(detail.getOpenid());
				
				Integer saveRes = partyService.publishParty(abs, detail);
				if(saveRes == null || saveRes == 0) {
					res.setCode("0003");
					res.setMessage("聚会信息发布失败,partyService.publishParty返回saveRes="+saveRes);
					LogUtil.error(new Exception("聚会信息发布失败,partyService.publishParty返回saveRes="+saveRes), getClass());
				}
				else {
					res.setCode("0000");
					res.setMessage("聚会信息发布成功");
				}
			}
		} catch (Exception e){
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("publishParty执行结束");
		pgStatisService.statisCall("publishParty", res.getCode(), res.getMessage());
		return res;
	}
	
	
	//更新
	@ApiOperation(value="更新聚会信息", notes="更新聚会摘要信息/详细信息")
	@RequestMapping(value="/updateParty.json",method=RequestMethod.POST)
	public JsonResult updateParty(@RequestBody PgDetail detail) {
		LogUtil.info("执行updateParty");
		JsonResult res = new JsonResult();
		try {
			if(detail == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				detail.setCreateTime(nousedate);
				detail.setUpdateTime(nousedate);
				
				PartyAbs abs = new PartyAbs();
				abs.setAppid(wxConfig.getAppid());
				abs.setPartyid(detail.getPartyid());
				abs.setPartyTime(detail.getPartyTime());
				abs.setPartyTitle(detail.getPartyTitle());
				abs.setUpperson(detail.getOpenid());
				abs.setStatus(1);
				abs.setUpdateTime(nousedate);
				
				Integer upRes = partyService.updateParty(abs, detail);
				if(upRes == null || upRes == 0) {
					res.setCode("0003");
					res.setMessage("聚会信息更新失败,partyService.updateParty返回upRes="+upRes);
					LogUtil.error(new Exception("聚会信息更新失败,partyService.updateParty返回upRes="+upRes), getClass());
				}
				else {
					res.setCode("0000");
					res.setMessage("聚会信息更新成功");
				}
			}
		} catch (Exception e){
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(e, getClass());
		}
		LogUtil.info("updateParty执行结束");
		pgStatisService.statisCall("updateParty", res.getCode(), res.getMessage());
		return res;
	}
	
}
