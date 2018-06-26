package com.partygo.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.codehaus.xfire.util.Base64;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import com.partygo.common.JsonResult;
import com.partygo.config.WxConfig;
import com.partygo.model.PgUser;
import com.partygo.service.PgStatisService;
import com.partygo.service.PgUserService;
import com.partygo.service.RedisService;
import com.partygo.service.WxJs2CodeService;
import com.partygo.util.EncryptUtil;
import com.partygo.util.LogUtil;
import com.partygo.util.MD5Util;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/hz")
@EnableAutoConfiguration
public class HzWxController {

	@Resource
	private WxJs2CodeService wxJs2CodeService;
	@Resource
	private WxConfig wxConfig;
	@Resource
	private PgStatisService pgStatisService;
	@Resource
	private RedisService redisService;
	@Resource
	private PgUserService pgUserService;
	
	@ApiOperation(value="获取hz用户openid", notes="获取hz用户openid")
	@GetMapping(value="/hzjs2session.json")
	public JsonResult hzjs2session(@RequestParam String code) {
		LogUtil.info("执行hzjs2session,code="+code);
		JsonResult res = new JsonResult();
		try {
			if(code == null || code.isEmpty()) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}
			else {
				String appid = wxConfig.getHzappid();
				String secret = wxConfig.getHzsecret();
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

					//存储洗白openid和openid的映射
					redisService.hashSet(outOpenid, "openid", openid);
					redisService.hashSet(outOpenid, "sessionkey", sessionKey);
					
					res.setCode("0000");
					res.setMessage("获取openid成功");
					res.setData(outOpenid);
					LogUtil.info("获取openid成功");
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		LogUtil.info("hjs2session执行结束");
		pgStatisService.statisCall("hzjs2session", res.getCode(), res.getMessage());
		return res;
		
	}
	
	@ApiOperation(value="获取hz用户UnionId", notes="获取hz用户UnionId")
	@PostMapping(value="/hzstoreUnionId.json")
	public JsonResult hzstoreUnionID(@RequestBody String obj) {
		LogUtil.info("执行hzstoreUnionID,obj=" + obj);
		JsonResult res = new JsonResult();
		try {
			if(StringUtils.isNullOrEmpty(obj)) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("请求参数为空"), getClass());
			}else {
				JSONObject json = JSON.parseObject(obj);
				String md5Openid = json.getString("openid");
				//缓存中是否存在sessionkey
				if(redisService.exists(md5Openid)) {
					String sessionKey = redisService.hashGet(md5Openid, "sessionkey").toString();
					if(StringUtils.isNullOrEmpty(sessionKey)) {
						res.setCode("0001");
						res.setMessage("session缓存不存在");
						LogUtil.error(new Exception("session缓存不存在，redisService.hashGet返回sessionKey=null"), getClass());
					}
					else {
						//解析参数，并进行AES-128-CBC解密
						String encryptedData = json.getString("encryptedData");
						String iv = json.getString("iv");
						String userInfo = EncryptUtil.decrypt(encryptedData,sessionKey,iv);
						if(StringUtils.isNullOrEmpty(userInfo)) {
							res.setCode("0003");
							res.setMessage("解密为空");
							LogUtil.error(new Exception("EncryptUtil.decrypt解密为空"), getClass());
						}
						else {
							JSONObject userJson = JSON.parseObject(userInfo);
							String openId = userJson.getString("openId");
							String nickName = Base64.encode(userJson.getString("nickName").getBytes());
							String unionId = userJson.getString("unionId");
							String avatarUrl = userJson.getString("avatarUrl");
							PgUser user1 = new PgUser();
							user1.setAppid(wxConfig.getHzappid());
							user1.setNickname(nickName);
							user1.setImage(avatarUrl);
							user1.setUnionid(unionId);
							user1.setOpenid(openId);
							Date date = new Date();       
							Timestamp nousedate = new Timestamp(date.getTime());
							user1.setCreateTime(nousedate);
							user1.setUpdateTime(nousedate);
							
							HashMap<String,String> map = new HashMap<String,String>();
							map.put("openid", md5Openid);
							map.put("nickName", nickName);
							map.put("image", avatarUrl);
							
							//查询数据库用户信息，如果不同则更新用户信息
							PgUser user2 = pgUserService.getUser(openId);
							//判断用户信息是否更新
							if(pgUserService.needUpdate(user1, user2)) {
								Integer insertRes = pgUserService.saveOrUpdatePgUser(user1);
								if(insertRes == null || insertRes == 0) {
									res.setCode("0003");
									res.setMessage("用户信息更新失败");
									LogUtil.error(new Exception("用户信息更新失败"), getClass());
								}
								else {
									res.setCode("0000");
									res.setMessage("用户信息更新成功");
									res.setData(JSON.toJSON(map));
									LogUtil.info("用户信息更新成功");
								}
							}
							else {
								res.setCode("0000");
								res.setMessage("用户信息成功返回");
								res.setData(JSON.toJSON(map));
								LogUtil.info("用户信息成功返回");
							}
							
						}
					}
				}
				else {
					res.setCode("0001");
					res.setMessage("缓存不存在");
					LogUtil.error(new Exception("缓存不存在"), getClass());
				}
			}
		}
		catch(Exception e){
			res.setCode("0002");
			res.setMessage("请求异常，error:"+e.getMessage());
			LogUtil.error(e, getClass());
		}
		LogUtil.info("hzstoreUnionID执行结束");
		pgStatisService.statisCall("hzstoreUnionID", res.getCode(), res.getMessage());
		return res;
	}
}
