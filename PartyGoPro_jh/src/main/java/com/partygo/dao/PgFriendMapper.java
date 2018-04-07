package com.partygo.dao;

import org.apache.ibatis.annotations.Param;

import com.partygo.model.PgFriend;

public interface PgFriendMapper {

    Integer insert(PgFriend record);

    Integer insertSelective(PgFriend record);

    Integer selectCountByOpenid(@Param("appid")String appid, @Param("openid1")String openid1, @Param("openid2")String openid2);
}