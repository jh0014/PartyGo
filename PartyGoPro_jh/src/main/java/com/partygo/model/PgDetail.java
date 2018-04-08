package com.partygo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PgDetail {
    private String partyid;

    private String partyTitle;
    
    private String openid;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date partyTime;

    private String partyAddress;

    private String partySound;

    private String partyImg;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid == null ? null : partyid.trim();
    }

    public String getPartyTitle() {
        return partyTitle;
    }

    public void setPartyTitle(String partyTitle) {
        this.partyTitle = partyTitle == null ? null : partyTitle.trim();
    }

    public Date getPartyTime() {
        return partyTime;
    }

    public void setPartyTime(Date partyTime) {
        this.partyTime = partyTime;
    }

    public String getPartyAddress() {
        return partyAddress;
    }

    public void setPartyAddress(String partyAddress) {
        this.partyAddress = partyAddress == null ? null : partyAddress.trim();
    }

    public String getPartySound() {
        return partySound;
    }

    public void setPartySound(String partySound) {
        this.partySound = partySound == null ? null : partySound.trim();
    }

    public String getPartyImg() {
        return partyImg;
    }

    public void setPartyImg(String partyImg) {
        this.partyImg = partyImg == null ? null : partyImg.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
    
    
}