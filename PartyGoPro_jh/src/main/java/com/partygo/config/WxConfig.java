package com.partygo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="wx")
public class WxConfig {
	
	private String appid;
	
	private String jscode2session;
	
	private String secret;
	
	private String granttype;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getGranttype() {
		return granttype;
	}

	public void setGranttype(String granttype) {
		this.granttype = granttype;
	}

	public String getJscode2session() {
		return jscode2session;
	}

	public void setJscode2session(String jscode2session) {
		this.jscode2session = jscode2session;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	
}
