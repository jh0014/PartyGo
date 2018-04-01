package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PartyAbsMapper;
import com.partygo.model.PartyAbs;

@Service("partyAbsService")
public class PartyAbsService {

	@Resource
	private PartyAbsMapper partyAbsMapper;
	
	public PartyAbs getPartyAbsById(String pid) {
		return partyAbsMapper.selectByPrimaryKey(pid);
	}
	
	public int addPartyAbs(PartyAbs record) {
		return partyAbsMapper.insertSelective(record);
	}
}
