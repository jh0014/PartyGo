package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.partygo.dao.PgUserMapper;
import com.partygo.model.PgUser;
import com.partygo.util.LogUtil;

@Service("pgUserService")
public class PgUserService {

	@Resource
	private PgUserMapper pgUserMapper;
	
	public Integer saveOrUpdatePgUser(PgUser user) {
		try {
			if(user == null)
				return 0;
			PgUser u = pgUserMapper.selectByPrimaryKey(user.getOpenid());
			//用户信息存在，执行更新
			if(u != null) {
				return pgUserMapper.update(user);
			}
			else {
				return pgUserMapper.insertSelective(user);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}

	
	public Integer getCount(String appid) {
		try {
			return pgUserMapper.getCount(appid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public PgUser getUser(String openid) {
		try {
			return pgUserMapper.selectByPrimaryKey(openid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public boolean needUpdate(PgUser user1, PgUser user2) {
		if(user1 == null || user2 == null)
			return true;
		if(!user1.getNickname().equals(user2.getNickname()))
			return true;
		else if(!user1.getImage().equals(user2.getImage()))
			return true;
		else
			return false;
	}
}
