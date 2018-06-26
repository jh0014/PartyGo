package com.partygo.service;
import javax.annotation.Resource;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;
import com.mysql.jdbc.StringUtils;
import com.partygo.model.PgUser;

@Service("gameUserService")
public class GameUserService {

	@Resource
	private RedisService redisService;
	@Resource
	private PgUserService pgUserService;
	
	public PgUser GetUserInfo(String openid){
		try {
			if(StringUtils.isNullOrEmpty(openid)) {
				return null;
			}
			//openid换取缓存中真实openid及用户信息
			if(!redisService.exists(openid)) {
				return null;
			}
			String realOpenid = redisService.hashGet(openid, "openid").toString();
			//根据openid查询用户信息
			PgUser user = pgUserService.getUser(realOpenid);
			String name = user.getNickname();
			user.setNickname(new String(Base64.decode(name.getBytes()),"UTF-8"));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
