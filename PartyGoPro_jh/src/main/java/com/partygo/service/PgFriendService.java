package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgFriendMapper;
import com.partygo.model.PgFriend;
import com.partygo.util.LogUtil;

@Service("pgFriendService")
public class PgFriendService {
	
	@Resource
	private PgFriendMapper pgFriendMapper;
	
	public Integer insertFriendRecord(PgFriend pf) {
		try {
			if(pf == null) {
				return 0;
			}
			Integer count = pgFriendMapper.selectCountByOpenid(pf.getAppid(), pf.getOpenid1(), pf.getOpenid2());
			if(count == null || count == 0) {
				return pgFriendMapper.insertSelective(pf);

			}
			else {
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			return 0;
		}
	}
}
