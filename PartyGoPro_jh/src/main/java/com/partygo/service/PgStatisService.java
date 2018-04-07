package com.partygo.service;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.partygo.dao.PgStatisMapper;
import com.partygo.model.PgStatis;
import com.partygo.util.LogUtil;

@Service("pgStatisService")
public class PgStatisService {
	
	@Resource
	private PgStatisMapper pgStatisMapper;
	
	public Integer statisCall(String api, String retCode, String retMsg) {
		try {
			if(api == null || api.isEmpty())
				return 0;
			if(retCode == null || retCode.isEmpty())
				return 0;
			if(retMsg == null || retMsg.isEmpty())
				return 0;
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			//插入新纪录
			PgStatis newPs = new PgStatis();
			newPs.setApiname(api);
			newPs.setRetcode(retCode);
			newPs.setRetmsg(retMsg);
			newPs.setCreateTime(nousedate);
			newPs.setUpdateTime(nousedate);
			return pgStatisMapper.insertSelective(newPs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			return 0;
		}
	}

}
