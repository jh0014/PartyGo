package com.partygo.dao;

import com.partygo.model.PgStatis;

public interface PgStatisMapper {

    Integer insert(PgStatis record);

    Integer insertSelective(PgStatis record);

    PgStatis selectByPrimaryKey(String apiname);

    Integer updateByPrimaryKeySelective(PgStatis record);

    Integer updateByPrimaryKey(PgStatis record);
}