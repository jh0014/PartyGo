package com.partygo.dao;

import com.partygo.model.PgDetail;

public interface PgDetailMapper {

    int insert(PgDetail record);

    int insertSelective(PgDetail record);

    PgDetail selectByPrimaryKey(String partyid);

    int updateByPrimaryKeySelective(PgDetail record);

    int updateByPrimaryKey(PgDetail record);
}