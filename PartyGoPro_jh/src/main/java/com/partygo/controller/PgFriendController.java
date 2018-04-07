package com.partygo.controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.partygo.common.JsonResult;
import com.partygo.config.WxConfig;
import com.partygo.model.PgFriend;
import com.partygo.service.PgFriendService;
import com.partygo.service.PgStatisService;
import com.partygo.util.LogUtil;

import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value="/pg")
@EnableAutoConfiguration
public class PgFriendController {
	
	@Resource
	private PgFriendService pgFriendService;
	@Resource
	private WxConfig wxConfig;
	@Resource
	private PgStatisService pgStatisService;

	@ApiOperation(value="保存好友关系信息", notes="保存好友关系信息")
	@RequestMapping(value="/savePgFriend.json",method=RequestMethod.POST)
	public JsonResult savePgFriend(@RequestBody PgFriend friend) {
		LogUtil.info("执行savePgFriend");
		JsonResult res = new JsonResult();
		try {
			if(friend == null) {
				res.setCode("0001");
				res.setMessage("请求数据为空");
				LogUtil.error(new Exception("好友关系信息为空"), getClass());
			}
			else {
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				friend.setCreateTime(nousedate);
				String tempOpenid1 = friend.getOpenid1();
				String tempOpenid2 = friend.getOpenid2();
				if(tempOpenid1.compareToIgnoreCase(tempOpenid2)<0) {
					friend.setOpenid1(tempOpenid1);
					friend.setOpenid2(tempOpenid2);
				}
				else {
					friend.setOpenid1(tempOpenid2);
					friend.setOpenid2(tempOpenid1);
				}
				friend.setAppid(wxConfig.getAppid());
				Integer count = pgFriendService.insertFriendRecord(friend);
				if(count == null) {
					res.setCode("0002");
					res.setMessage("保存好友关系信息异常，count = null");
					LogUtil.error(new Exception("pgFriendService.insertFriendRecord返回count=null"), getClass());
				}
				else {
					if(count == 1) {
						res.setCode("0000");
						res.setMessage("保存好友关系信息成功");
						LogUtil.info("保存好友关系信息成功");
					}
					else {
						res.setCode("0003");
						res.setMessage("保存好友关系信息失败");
						LogUtil.error(new Exception("pgFriendService.insertFriendRecord返回count="+count), getClass());
					}
				}
			}
		}
		catch(Exception e) {
			res.setCode("0002");
			res.setMessage("请求异常");
			LogUtil.error(new Exception(e), getClass());
		}
		pgStatisService.statisCall("savePgFriend", res.getCode(), res.getMessage());
		LogUtil.info("savePgFriend执行结束");
		return res;
	}
}
