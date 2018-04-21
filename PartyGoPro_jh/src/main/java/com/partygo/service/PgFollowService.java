package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgFollowMapper;
import com.partygo.model.PgFollow;

@Service("pgFollowService")
public class PgFollowService {

	@Resource
	private PgFollowMapper pgFollowMapper;
	
	public Integer savePgFollow(PgFollow follow) {
		if(follow == null) {
			return 0;
		}
		if(pgFollowMapper.selectById(follow.getOpenid(),follow.getPartyid()) != null)
			return 0;
		return pgFollowMapper.insertSelective(follow);
	}
	
	public Integer updatePgFollow(PgFollow follow) {
		if(pgFollowMapper.selectById(follow.getOpenid(),follow.getPartyid()) == null)
			return 0;
		return pgFollowMapper.updateBySelective(follow);
	}
	
	public PgFollow selectById(String openid, String partyid) {
		if(openid.isEmpty() || partyid.isEmpty())
			return null;
		return pgFollowMapper.selectById(openid, partyid);
	}
}
