package com.partygo.controller;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.config.WxConfig;
import com.partygo.model.PgDetail;
import com.partygo.service.PartyAbsService;
import com.partygo.service.PartyDetailService;
import com.partygo.service.PgStatisService;
import com.partygo.util.LogUtil;

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
	
	//发布
	
	//更新
	
	//删除
	
}
