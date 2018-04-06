package com.partygo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.partygo.model.PartyAbs;


public interface PartyAbsMapper {

    Integer deleteByPrimaryKey(String partyid);

    Integer insert(PartyAbs record);

    Integer insertSelective(PartyAbs record);

    List<PartyAbs> selectByExample(PartyAbs example);
    
    List<PartyAbs> selectListByOpenid(String openid);

    PartyAbs selectByPrimaryKey(String partyid);

    Integer updateByExampleSelective(@Param("record") PartyAbs record, @Param("example") PartyAbs example);

    Integer updateByExample(@Param("record") PartyAbs record, @Param("example") PartyAbs example);

    Integer updateByPrimaryKeySelective(PartyAbs record);

    Integer updateByPrimaryKey(PartyAbs record);
}
