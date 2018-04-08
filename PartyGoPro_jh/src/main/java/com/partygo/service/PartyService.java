package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.partygo.dao.PartyAbsMapper;
import com.partygo.dao.PgDetailMapper;
import com.partygo.model.PartyAbs;
import com.partygo.model.PgDetail;
import com.partygo.util.LogUtil;

@Service("partyService")
public class PartyService {

	@Resource
	private PartyAbsMapper partyAbsMapper;
	@Resource
	private PgDetailMapper pgDetailMapper;
	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT) 
	public Integer deletePartyInfo(String pid) {
		try {
			if(pid == null) {
				return 0;
			}
			
			Integer absRes = partyAbsMapper.deleteByPrimaryKey(pid);
			Integer detailRes = pgDetailMapper.deleteByPrimaryKey(pid);
			
			return absRes & detailRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT) 
	public Integer publishParty(PartyAbs abs, PgDetail detail) {
		try {
			if(abs == null || detail == null) {
				return 0;
			}
			//查询当前存在数据
			if(partyAbsMapper.selectByPrimaryKey(abs.getPartyid()) != null && pgDetailMapper.selectByPrimaryKey(detail.getPartyid()) != null)
				return 0;
			Integer absRes = partyAbsMapper.insertSelective(abs);
			Integer detailRes = pgDetailMapper.insertSelective(detail);
			
			return absRes & detailRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT) 
	public Integer updateParty(PartyAbs abs, PgDetail detail) {
		try {
			if(abs == null || detail == null) {
				return 0;
			}
			//查询当前存在数据
			if(partyAbsMapper.selectByPrimaryKey(abs.getPartyid()) == null || pgDetailMapper.selectByPrimaryKey(detail.getPartyid()) == null)
				return 0;
			Integer absRes = partyAbsMapper.updateByPrimaryKeySelective(abs);
			Integer detailRes = pgDetailMapper.updateByPrimaryKeySelective(detail);
			
			return absRes & detailRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
}
