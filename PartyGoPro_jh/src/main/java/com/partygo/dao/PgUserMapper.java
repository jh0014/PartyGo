package com.partygo.dao;

import com.partygo.model.PgUser;

public interface PgUserMapper {
  
    Integer insertSelective(PgUser record);

    Integer update(PgUser record);
    
    Integer getCount(String appid);
    
    PgUser selectByPrimaryKey(String openid);
}