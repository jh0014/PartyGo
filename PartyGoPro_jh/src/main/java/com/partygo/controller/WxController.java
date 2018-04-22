package com.partygo.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import com.partygo.common.JsonResult;
import com.partygo.config.WxConfig;
import com.partygo.service.PgStatisService;
import com.partygo.service.RedisService;
import com.partygo.service.WxJs2CodeService;
import com.partygo.util.LogUtil;
import com.partygo.util.MD5Util;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class WxController {

	@Resource
	private WxJs2CodeService wxJs2CodeService;
	@Resource
	private WxConfig wxConfig;
	@Resource
	private PgStatisService pgStatisService;
	@Resource
	private RedisService redisService;
	
	@ApiOperation(value="获取用户openid", notes="获取用户openid")
	@PostMapping(value="/js2session.json")
	public JsonResult js2session(@RequestBody String code) {
		LogUtil.info("执行js2session,code="+code);
		JsonResult res = new JsonResult();
		try {
			if(code == null || code.isEmpty()) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				String appid = wxConfig.getAppid();
				String secret = wxConfig.getSecret();
				String grantType = wxConfig.getGranttype();
				String jscode2session = wxConfig.getJscode2session();
				StringBuffer sb = new StringBuffer();
				sb.append(jscode2session).append("?appid="+appid).append("&secret="+secret).append("&js_code="+code).append("&grant_type="+grantType);
				String url = sb.toString();
				JSONObject wxRes = wxJs2CodeService.getOpenId(url);
				if(wxRes == null) {
					res.setCode("0003");
					res.setMessage("获取openid失败");
					LogUtil.error(new Exception("获取openid失败，wxJs2CodeService.getOpenId返回wxRes=null"), getClass());
				}
				String openid = wxRes.getString("openid");
				String sessionKey = wxRes.getString("session_key");
				if(openid == null || sessionKey == null || openid.isEmpty() || sessionKey.isEmpty()) {
					String errcode = wxRes.get("errcode").toString();
					String errmsg = wxRes.getString("errmsg");
					res.setCode("0003");
					res.setMessage("获取openid失败,errcode="+errcode+",errormsg="+errmsg);
					LogUtil.error(new Exception("获取openid失败，wx返回错误，errcode="+errcode+",errormsg="+errmsg), getClass());
				}
				else {
					//将openid洗白
					String md5Openid = MD5Util.MD5(openid);
					String TimeNow = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());  
					String outOpenid = md5Openid + TimeNow;
					//查询redis是否存在缓存openid
					String tmpOpenid = (String) redisService.hashGet(outOpenid, "openid");
					String tmpSessionKey = (String) redisService.hashGet(outOpenid, "sessionkey");
					if(StringUtils.isNullOrEmpty(tmpOpenid) || StringUtils.isNullOrEmpty(tmpSessionKey)){
						//存储洗白openid和openid的映射
						redisService.hashSet(outOpenid, "openid", openid, 24);
						redisService.hashSet(outOpenid, "sessionkey", sessionKey, 24);
					}
					
					res.setCode("0000");
					res.setMessage("获取openid成功");
					res.setData(md5Openid);
					LogUtil.info("获取openid成功");
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		LogUtil.info("js2session执行结束");
		pgStatisService.statisCall("js2session", res.getCode(), res.getMessage());
		return res;
		
	}
}
