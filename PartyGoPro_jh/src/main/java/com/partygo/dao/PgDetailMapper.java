package com.partygo.dao;

import com.partygo.model.PgDetail;

public interface PgDetailMapper {

	Integer insert(PgDetail record);

    Integer insertSelective(PgDetail record);

    PgDetail selectByPrimaryKey(String partyid);

    Integer updateByPrimaryKeySelective(PgDetail record);

    Integer updateByPrimaryKey(PgDetail record);
    
    Integer deleteByPrimaryKey(String partyid);
}