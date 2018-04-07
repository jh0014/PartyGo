package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgUserInfoMapper;
import com.partygo.model.PgUser;
import com.partygo.util.LogUtil;

@Service("pgUserService")
public class PgUserService {

	@Resource
	private PgUserInfoMapper pgUserMapper;
	
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
}
