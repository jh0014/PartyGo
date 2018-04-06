package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgUserMapper;
import com.partygo.model.PgUser;
import com.partygo.util.LogUtil;

@Service("pgUserService")
public class PgUserService {

	@Resource
	private PgUserMapper pgUserMapper;
	
	public Integer savePgUser(PgUser user) {
		try {
			if(user == null)
				return 0;
			PgUser u = pgUserMapper.selectByPrimaryKey(user.getOpenid());
			//用户信息存在，无法插入
			if(u != null)
				return 2;
			return pgUserMapper.insertSelective(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public Integer updatePgUser(PgUser user) {
		try {
			if(user == null)
				return 0;
			PgUser u = pgUserMapper.selectByPrimaryKey(user.getOpenid());
			//用户信息不存在，无法更新
			if(u == null)
				return 2;
			return pgUserMapper.update(user);
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
}
