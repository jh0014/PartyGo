package com.partygo.dao;

import com.partygo.model.PgFollow;
import org.apache.ibatis.annotations.Param;

public interface PgFollowMapper {

    int insert(PgFollow record);

    int insertSelective(PgFollow record);

    int updateBySelective(@Param("record") PgFollow record);
    
    PgFollow selectById(String openid, String partyid);

}