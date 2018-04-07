package com.partygo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.partygo.util.LogUtil;

@Service("wxJs2CodeService")
public class WxJs2CodeService {

	@Autowired
	private RestTemplate restTemplate;
	
	public JSONObject getOpenId(String url){	
		//post json数据
		try {
			if(url == null) {
				return null;
			}
			//get json数据
			JSONObject res = restTemplate.getForEntity(url, JSONObject.class).getBody();
			return res;
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error(e, getClass());
			throw e;
		}
	}
}
