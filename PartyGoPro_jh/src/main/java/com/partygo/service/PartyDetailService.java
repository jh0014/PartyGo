package com.partygo.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgDetailMapper;
import com.partygo.model.PgDetail;
import com.partygo.util.LogUtil;

@Service("partyDetailService")
public class PartyDetailService {

	@Resource
	private PgDetailMapper pgDetailMapper;
	
	public PgDetail selectDetailByPid(String pid) {
		try {
			if(pid == null || pid.isEmpty()) {
				return null;
			}
			return pgDetailMapper.selectByPrimaryKey(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public Integer saveOrUpdateDetail(PgDetail detail) {
		try {
			if(detail == null) {
				return 0;
			}
			
			//查询是否有detail
			String pid = detail.getPartyid();
			if(pgDetailMapper.selectByPrimaryKey(pid) == null) {
				//不存在则插入
				return pgDetailMapper.insertSelective(detail);
			}
			else {
				//存在则更新
				return pgDetailMapper.updateByPrimaryKeySelective(detail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
	
	public Integer deleteDetailById(String pid) {
		try {
			if(pid == null)
				return null;
			return pgDetailMapper.deleteByPrimaryKey(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
}
