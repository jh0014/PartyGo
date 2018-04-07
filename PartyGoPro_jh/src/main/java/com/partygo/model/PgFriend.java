package com.partygo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PgFriend {
    private String appid;

    private String openid1;

    private String openid2;
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getOpenid1() {
        return openid1;
    }

    public void setOpenid1(String openid1) {
        this.openid1 = openid1 == null ? null : openid1.trim();
    }

    public String getOpenid2() {
        return openid2;
    }

    public void setOpenid2(String openid2) {
        this.openid2 = openid2 == null ? null : openid2.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}