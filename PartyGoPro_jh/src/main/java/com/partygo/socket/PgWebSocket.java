package com.partygo.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
/**
 * 游戏（状态、人数、房间号、类型、用户）用户（状态，游戏值）
 * 创建房间时，{key}就是当前用户的openid，加入房间时，key是房主的房间号，num是房主设置的人数，type是创建或加入
 * {openid}是当前用户的
 * @author Jeremy
 *
 */
@Component
@ServerEndpoint("/pgsocket/{type}/{rid}/{openid}/{num}")
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
	public void onOpen(Session session,@PathParam(value="type") String type, @PathParam(value="rid") String rid, @PathParam(value="openid") String openid, @PathParam(value="num") String num) {
		try {
			LogUtil.info("执行onOpen,id="+session.getId()+",openid="+openid+",rid="+rid+",num="+num+",type="+type);
			this.session = session;
			addOnlineCount();
			JSONObject data = new JSONObject();
			data.put("openid", openid);
			data.put("state", "0");
			data.put("value", "-1");
			String resStr = null;
			JSONObject res = new JSONObject(); 
			if(redisService == null)
				redisService = applicationContext.getBean(RedisService.class);
			//查询type，若为1则创建，其他为加入
			if(type.equals("1")) {
				//创建用户数据
				redisService.hashSet(rid, openid, data.toJSONString(), 3, "min");
				//设置房间人数
				redisService.hashSet(rid, "num", num, 3, "min");
				res.put("type", "open");
				res.put("rid", rid);
				res.put("openid", openid);
				res.put("msg", "成功创建房间");
				resStr = res.toJSONString();
				
			}else{
				//如果玩家之前加入过，且未过期，则不用重置数据
				if(redisService.hashGet(rid, openid) == null) {
					//玩家未加入或者失效
					redisService.hashSet(rid, openid, data.toJSONString(), 3, "min");
				}
				res.put("type", "open");
				res.put("rid", rid);
				res.put("openid", openid);
				res.put("msg", "成功加入房间");
				resStr = res.toJSONString();
			}
			socketTable.put(rid+session.getId(), this);
			sendInfoToAll(rid, resStr);
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
	public void onClose(Session session,@PathParam(value="rid") String rid, @PathParam(value="openid") String openid) {
		LogUtil.info("执行onClose,id="+session.getId()+",openid="+openid+",rid="+rid);		
		try {
			socketTable.remove(rid+session.getId());
			subOnlineCount();
			//删除redis消息
			if(redisService == null)
				redisService = applicationContext.getBean(RedisService.class);
			JSONObject res = new JSONObject(); 
			if(redisService.hashGet(rid, openid) != null) {
				//查询用户是否已经完成游戏
				String objStr = redisService.hashGet(rid, openid).toString();
				JSONObject jsonVal = JSONObject.parseObject(objStr);
				String state = jsonVal.getString("state");
				if(state.equals("0")) {
					//未完成游戏就退出的用户，直接删除数据
					redisService.hashDel(rid, openid);
					res.put("type", "close");
					res.put("rid", rid);
					res.put("openid", openid);
					res.put("msg", "退出房间");
					String response = res.toJSONString();
					sendInfoToAll(rid,response);
				}
				
				//因为有一条为房间人数，因此1则为空房间，2则为只剩下一个用户且已完成游戏，删除房间
				if(redisService.hashLen(rid) <= 2 && getOnlineCount() <= 2) {
					//删除房间之前判断用户完成程度，输出给最后一个玩家
					List<JSONObject> list = new ArrayList<JSONObject>();
					for(Object item : redisService.hashKeys(rid)) {
						if(item.toString().equals("num"))
							continue;
						Object obj = redisService.hashGet(rid, item.toString());
						if(obj != null) {
							JSONObject json = JSONObject.parseObject(obj.toString());
							if(json.getString("state").toString().equals("1")) {
								list.add(json);
							}
						}
					}
					
					redisService.remove(rid);
					String response = "";
					if(list.size() == 0) {
						res.put("type", "close");
						res.put("rid", rid);
						res.put("openid", openid);
						res.put("msg", "你的小伙伴都离开了，快去找他们算账吧~");
						response = res.toJSONString();
					}else {
						res.put("type", "close");
						res.put("rid", rid);
						res.put("openid", openid);
						res.put("list", list);
						res.put("msg", "你是坚持到最后的~");
						response = res.toJSONString();
					}
					
					sendInfoToAll(rid,response);
				}
	
			}
		} catch (Exception e) {
			LogUtil.error(e,getClass());
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
	public void onMessage(Session session, String message, @PathParam(value="rid")String rid, @PathParam(value="openid") String openid) {
		try {
			LogUtil.info("执行onMessage,id="+session.getId()+",openid="+openid+",message="+message+",rid="+rid);
			JSONObject res = new JSONObject(); 
			if(redisService == null)
				redisService = applicationContext.getBean(RedisService.class);
			Object objVal = redisService.hashGet(rid, openid);
			if(objVal != null) {
				//如果用户状态为0则更新用户状态及数据，否则不更新，已经有数据了
				JSONObject jsonVal = JSONObject.parseObject(objVal.toString());
				if(jsonVal.getString("state").equals("0")) {
					jsonVal.put("state", "1");
					jsonVal.put("value", message);
					redisService.hashSet(rid, openid, jsonVal.toJSONString(), 3, "min");
				}else {
					res.put("type", "send");
					res.put("state", "1");
					res.put("msg", "你已经玩过一次啦，请等待其他玩家~");
					String response = res.toJSONString();
					sendMessage(response);
				}
				
			}
			
			//查询所有用户状态是否均完成游戏
			//所有用户都完成游戏，则排序，发送广播
			Set<Object> keys = redisService.hashKeys(rid);
			List<JSONObject> list = new ArrayList<JSONObject>();
			boolean flag = true; //标识所有用户游戏状态，true已完成，false未完成
			for(Object item : keys) {
				if(item.toString().equals("num"))
					continue;
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
				//所有玩家都完成游戏则删除房间
				redisService.remove(rid);
				res.put("type", "send");
				res.put("state", "0");
				res.put("msg", "快来看看结果吧~");
				res.put("data", list);
				String response = res.toJSONString();
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
