package com.partygo.socket;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import com.partygo.model.PgUser;
import com.partygo.service.GameUserService;
import com.partygo.service.RedisService;
import com.partygo.util.LogUtil;
/*
 * order:id_val,
 * id:0--创建房间 1--加入房间 2--开始游戏 3--生成游戏值 4--获取游戏结果 5--退出房间 6--关闭房间
 * 
 * game_status:0--未开始 1--已开始 2--已结束
 * 
 * person_state:0--未开始 1--已完成 2--退出
 * */

@Component
@ServerEndpoint("/HZQDChoose/{game}/{rid}/{openid}/{order}")
public class HZQDChooseGame {
		//用于记录当前在线连接数
		private static int onlineCount = 0;
		//用于存放每个客户端socket
		private static ConcurrentHashMap<String,HZQDChooseGame> socketTable = new ConcurrentHashMap<String,HZQDChooseGame>();
		//与某个客户端连接的session，通过它给客户端发送数据
		private Session session;
		
		private static ApplicationContext applicationContext;
		
		public static void setApplicationContext(ApplicationContext context) {
			applicationContext = context;
		}
		
		private RedisService redisService;
		
		private GameUserService gameUserService;
		
		/**
		 * 在线人数增加
		 */
		public static synchronized void addOnlineCount() {
			HZQDChooseGame.onlineCount++;
	    }
		
		/**
		 * 在线人数减少
		 */
		public static synchronized void subOnlineCount() {
			HZQDChooseGame.onlineCount--;
	    }
		
		/**
		 * 获取在线人数
		 * @return
		 */
		public static synchronized int getOnlineCount() {
	        return HZQDChooseGame.onlineCount;
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
			for (Entry<String, HZQDChooseGame> entry : socketTable.entrySet()) {
				String key = entry.getKey();
				if(key.startsWith(rid)) {
					try {
						HZQDChooseGame item = entry.getValue();
		                item.sendMessage(message);
		                LogUtil.info("成功发送一条消息:" + message);  
		            } catch (IOException e) {
		                continue;
		            }
				}
			}
	    }
		
		/**
		 * 创建/加入房间
		 * @param session
		 * @param game
		 * @param rid
		 * @param openid
		 * @param order
		 */
		@OnOpen
		public void onOpen(Session session,@PathParam(value="game") String game, @PathParam(value="rid") String rid, @PathParam(value="openid") String openid, @PathParam(value="order") String order) {
			try {
				LogUtil.info("执行onOpen,id="+session.getId()+",openid="+openid+",rid="+rid+",order="+order+",game="+game);
				this.session = session;
				if(redisService == null)
					redisService = applicationContext.getBean(RedisService.class);
				if(gameUserService == null)
					gameUserService = applicationContext.getBean(GameUserService.class);
				JSONObject data = new JSONObject();
				data.put("openid", openid);
				data.put("state", "0");
				data.put("value", "-1");
				String resStr = null;
				JSONObject res = new JSONObject(); 
				//查询用户信息
				PgUser user = gameUserService.GetUserInfo(openid);
				if(user == null) {
					sendMessage("未获取到你的信息，请稍后再试~");
					return;
				}
				String realOpenid = user.getOpenid();
				//查询order，若为0则创建，其他为加入
				if(order.equals("0")) {
					//创建用户数据
					redisService.hashSet(rid, realOpenid, data.toJSONString(), 10, "min");
					//设置房间人数
					String num = order.split("_")[1];
					redisService.hashSet(rid, "num", num, 10, "min");
					redisService.hashSet(rid, "status", "0", 10, "min");
					res.put("type", "open");
					res.put("rid", rid);
					res.put("openid", openid);
					res.put("name", user.getNickname());
					res.put("img", user.getImage());
					res.put("owner", "1"); //是否是房主
					res.put("msg", "成功创建房间");
					resStr = res.toJSONString();
					
				}else{
					//如果玩家之前加入过，且未过期，则不用重置数据
					if(redisService.hashGet(rid, realOpenid) == null) {
						//玩家未加入或者失效
						redisService.hashSet(rid, realOpenid, data.toJSONString(), 10, "min");
						//刷新房间有效期
						redisService.expire(rid, 10L, "min");
					}
					res.put("type", "open");
					res.put("rid", rid);
					res.put("openid", openid);
					res.put("name", user.getNickname());
					res.put("img", user.getImage());
					res.put("owner", "0");
					res.put("msg", "成功加入房间");
					resStr = res.toJSONString();
				}
				socketTable.put(rid+session.getId(), this);
				sendInfoToAll(rid, resStr);
				
			}catch(Exception e) {
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
				if(gameUserService == null)
					gameUserService = applicationContext.getBean(GameUserService.class);
				//查询用户信息
				PgUser user = gameUserService.GetUserInfo(openid);
				if(user == null) {
					sendMessage("未获取到你的信息，请稍后再试~");
					return;
				}
				String realOpenid = user.getOpenid();
				JSONObject res = new JSONObject(); 
				if(redisService.hashGet(rid, realOpenid) != null) {
					//查询用户是否已经完成游戏
					String objStr = redisService.hashGet(rid, realOpenid).toString();
					JSONObject jsonVal = JSONObject.parseObject(objStr);
					String state = jsonVal.getString("state");
					if(state.equals("0")) {
						//未完成游戏就退出的用户，直接删除数据
						redisService.hashDel(rid, openid);
						res.put("type", "close");
						res.put("rid", rid);
						res.put("openid", openid);
						res.put("name", user.getNickname());
						res.put("img", user.getImage());
						res.put("msg", "退出房间");
						String response = res.toJSONString();
						sendInfoToAll(rid,response);
					}
					
					//查看游戏状态是否已结束
					String status = redisService.hashGet(rid, "status").toString();
					if(StringUtils.isNullOrEmpty(status))
						redisService.remove(rid);
					if(status.equals("2"))
						redisService.remove(rid);
					
					//如果房间最后一人退出，也删除房间
					if(redisService.hashLen(rid) <= 3) {
						redisService.remove(rid);
					}
				}
		
			} catch (Exception e) {
				LogUtil.error(e,getClass());
			}	
		}
		
		/**
		 * 执行命令式调用
		 * @param session
		 * @param message
		 * @param rid
		 * @param openid
		 * @param game
		 * @param order
		 */
		@OnMessage
		public void onMessage(Session session, String message, @PathParam(value="rid")String rid, @PathParam(value="openid") String openid, @PathParam(value="game") String game, @PathParam(value="order") String order) {
			try {
				LogUtil.info("执行onMessage,id="+session.getId()+",openid="+openid+",message="+message+",rid="+rid+",game:"+game);
				JSONObject res = new JSONObject(); 
				if(redisService == null)
					redisService = applicationContext.getBean(RedisService.class);
				if(gameUserService == null)
					gameUserService = applicationContext.getBean(GameUserService.class);
				String response = "";
				if(game.equals("single")) {
					if(order.equals("3")) {
						//将游戏状态设置为已开始
						redisService.hashSet(rid, "status", "1", 10, "min");
						//将用户状态设置为已完成
						for(Object item : redisService.hashKeys(rid)) {
							if(item.toString().equals("num") || item.toString().equals("status"))
								continue;
							Object obj = redisService.hashGet(rid, item.toString());
							if(obj != null) {
								JSONObject json = JSONObject.parseObject(obj.toString());
								json.put("state", "1");
								redisService.hashSet(rid, item.toString(), json, 10, "min");
							}
						}
						
						//生成随机数
						Random rand = new Random();
						Integer i = rand.nextInt(15);
						res.put("index", i);
						res.put("msg", "选人开始");
						response = res.toJSONString();
						sendInfoToAll(rid,response);
					}
					else if(order.equals("4")) {
						res.put("result", message);
						res.put("msg", "选人结束");
						response = res.toJSONString();
						sendInfoToAll(rid,response);
					}
				}
				else if(game.equals("multi")) {
					
				}else {
					
				}
				
			} catch (Exception e) {
				LogUtil.error(e,getClass());
			}
		}
}
