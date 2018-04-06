package com.partygo.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PartyAbsMapper;
import com.partygo.model.PartyAbs;
import com.partygo.util.LogUtil;

@Service("partyAbsService")
public class PartyAbsService {

	@Resource
	private PartyAbsMapper partyAbsMapper;
	
	public PartyAbs getPartyAbsById(String pid) {	
		try {
			if(pid == null)
				return null;
			return partyAbsMapper.selectByPrimaryKey(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public Integer addPartyAbs(PartyAbs record) {
		try {
			if(record == null)
				return 0;
			PartyAbs abs = partyAbsMapper.selectByPrimaryKey(record.getPartyid());
			if(abs != null)
				return 3;
			return partyAbsMapper.insertSelective(record);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public List<PartyAbs> getPartyAbsListByOpenId(String openid) {
		try {
			if(openid == null)
				return null;
			return partyAbsMapper.selectListByOpenid(openid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
}
