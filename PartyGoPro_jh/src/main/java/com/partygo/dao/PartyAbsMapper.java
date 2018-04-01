package com.partygo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.partygo.model.PartyAbs;


public interface PartyAbsMapper {

    int deleteByPrimaryKey(String partyid);

    int insert(PartyAbs record);

    int insertSelective(PartyAbs record);

    List<PartyAbs> selectByExample(PartyAbs example);
    
    List<PartyAbs> selectListByPid(String pid);

    PartyAbs selectByPrimaryKey(String partyid);

    int updateByExampleSelective(@Param("record") PartyAbs record, @Param("example") PartyAbs example);

    int updateByExample(@Param("record") PartyAbs record, @Param("example") PartyAbs example);

    int updateByPrimaryKeySelective(PartyAbs record);

    int updateByPrimaryKey(PartyAbs record);
}
