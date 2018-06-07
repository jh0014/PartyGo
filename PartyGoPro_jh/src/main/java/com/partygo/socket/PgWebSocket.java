package com.partygo.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.partygo.service.RedisService;
import com.partygo.util.LogUtil;
import com.partygo.util.UuidUtil;
/**
 * 游戏（状态、人数、房间号、类型、用户）用户（状态，游戏值）
 * 创建房间时，{key}就是当前用户的openid，加入房间时，key是房主的openid
 * {openid}是当前用户的
 * @author Jeremy
 *
 */
@Component
@ServerEndpoint("/pgsocket/{key}/{openid}")
public class PgWebSocket {

	//用于记录当前在线连接数
	private static int onlineCount = 0;
	//用于存放每个客户端socket
	private static ConcurrentHashMap<String,PgWebSocket> socketTable = new ConcurrentHashMap<String,PgWebSocket>();
	//与某个客户端连接的session，通过它给客户端发送数据
	private Session session;
	
	private static ApplicationContext applicationContext;
	
	public static void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}
	
	private RedisService redisService;
	
	/**
	 * 连接建立成功调用方法
	 * @param session
	 * 用户state：0=加入，1=完成
	 */
	@OnOpen
	public void onOpen(Session session,@PathParam(value="key") String key, @PathParam(value="openid") String openid) {
		try {
			LogUtil.info("执行onOpen,id="+session.getId()+",openid="+openid+",key="+key);
			this.session = session;
			addOnlineCount();
			JSONObject data = new JSONObject();
			data.put("openid", openid);
			data.put("state", "0");
			data.put("value", "-1");
			String response = null;
			String rid = UuidUtil.generateRoomId(key, new Date());
			socketTable.put(rid+session.getId(), this);
			if(redisService == null)
				redisService = applicationContext.getBean(RedisService.class);
			//查询redis是否存在该key，存在则房间已经存在，用户是从用户进入房间,redis建立用户数据，发送成功加入消息
			//若不存在key，用户是房主，redis建立用户数据，发送成功建房消息
			if(redisService.exists(rid)) {
				response = openid+"成功加入"+key+"的房间";
			}
			else {
				response = openid+"成功创建房间";
			}
			redisService.hashSet(rid, session.getId(), data.toJSONString(), 3, "min");
			
			sendInfoToAll(rid, response);
		}
		catch(Exception e) {
			LogUtil.error(e,getClass());
			try {
				sendMessage("建立/加入房间异常");
			} catch (IOException e1) {
				LogUtil.error(e1,getClass());
			}
		}
	}
	
	/**
	 * 关闭连接时调用方法
	 */
	@OnClose
	public void onClose(Session session,@PathParam(value="key") String key, @PathParam(value="openid") String openid) {
		LogUtil.info("执行onClose,id="+session.getId()+",openid="+openid+",key="+key);		
		String rid = UuidUtil.generateRoomId(key, new Date());
		socketTable.remove(rid+session.getId());
		subOnlineCount();
		//删除redis消息
		if(redisService == null)
			redisService = applicationContext.getBean(RedisService.class);
		
		if(redisService.hashGet(rid, session.getId()) != null) {
			//查询用户是否已经完成游戏
			String objStr = redisService.hashGet(rid, session.getId()).toString();
			JSONObject jsonVal = JSONObject.parseObject(objStr);
			String state = jsonVal.getString("state");
			if(state.equals("0")) {
				//未完成游戏就退出的用户，直接删除数据
				redisService.hashDel(rid, session.getId());
			}
			
			//0则为空房间，1则为只剩下一个用户且已完成游戏，删除房间
			if(redisService.hashLen(rid) <= 1 && getOnlineCount() <= 1) {
				redisService.remove(rid);
			}

		}
		
	}
	
	/**
	 * 连接错误时调用方法
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		LogUtil.error(new Exception(error), getClass());
	}
	
	/**
	 * 收到客户端消息时调用方法
	 * @param session
	 * @param message
	 */
	@OnMessage
	public void onMessage(Session session, String message, @PathParam(value="key") String key, @PathParam(value="openid") String openid) {
		try {
			LogUtil.info("执行onMessage,id="+session.getId()+",key="+key+",openid="+openid+",message="+message);
			String rid = UuidUtil.generateRoomId(key, new Date());
			if(redisService == null)
				redisService = applicationContext.getBean(RedisService.class);
			Object objVal = redisService.hashGet(rid, session.getId());
			if(objVal != null) {
				//更新用户状态及数据
				JSONObject jsonVal = JSONObject.parseObject(objVal.toString());
				jsonVal.put("state", "1");
				jsonVal.put("value", message);
				redisService.hashSet(rid, session.getId(), jsonVal.toJSONString(), 3, "min");
			}
			
			//查询所有用户状态是否均完成游戏
			//所有用户都完成游戏，则排序，发送广播
			Set<Object> keys = redisService.hashKeys(rid);
			List<JSONObject> list = new ArrayList<JSONObject>();
			boolean flag = true; //标识所有用户游戏状态，true已完成，false未完成
			for(Object item : keys) {
				Object obj = redisService.hashGet(rid, item.toString());
				if(obj != null) {
					JSONObject json = JSONObject.parseObject(obj.toString());
					list.add(json);
					if(json.getString("state").equals("0")) {
						flag = false;
						break;
					}
				}
			}
			
			if(flag) {
				//所有用户都完成游戏
				Collections.sort(list, new Comparator<JSONObject>() {

					@Override
					public int compare(JSONObject json1, JSONObject json2) {
						// TODO Auto-generated method stub
						int val1 = Integer.parseInt(json1.getString("value"));
						int val2 = Integer.parseInt(json2.getString("value"));
						
						return val1-val2;
					}
					
				});
				String response = JSONObject.toJSONString(list);
				sendInfoToAll(rid,response);
			}
		} catch (Exception e) {
			LogUtil.error(e,getClass());
		} 
	}
	
	/**
	 * 发送消息给客户端
	 */
	public void sendMessage (String message) throws IOException {  
        this.session.getBasicRemote().sendText(message);    
        LogUtil.info("成功发送一条消息:" + message);  
    }  
	
	/**
	 * 群发游戏结果给同一个房间内客户端发消息
	 */
	public static void sendInfoToAll(String rid, String message) throws IOException {
		for (Entry<String, PgWebSocket> entry : socketTable.entrySet()) {
			String key = entry.getKey();
			if(key.startsWith(rid)) {
				try {
					PgWebSocket item = entry.getValue();
	                item.sendMessage(message);
	                LogUtil.info("成功发送一条消息:" + message);  
	            } catch (IOException e) {
	                continue;
	            }
			}
		}
    }
	
	/**
	 * 在线人数增加
	 */
	public static synchronized void addOnlineCount() {
		PgWebSocket.onlineCount++;
    }
	
	/**
	 * 在线人数减少
	 */
	public static synchronized void subOnlineCount() {
		PgWebSocket.onlineCount--;
    }
	
	/**
	 * 获取在线人数
	 * @return
	 */
	public static synchronized int getOnlineCount() {
        return onlineCount;
    }
	
}
