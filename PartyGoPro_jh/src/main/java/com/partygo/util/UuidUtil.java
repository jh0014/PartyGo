package com.partygo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UuidUtil {
	
	private static String string = "0123456789abcdefghijklmnopqrstuvwxyz";   
	
	private static int getRandom(int count) {
	    return (int) Math.round(Math.random() * (count));
	}
	 
	private static String getRandomString(int length){
	    StringBuffer sb = new StringBuffer();
	    int len = string.length();
	    for (int i = 0; i < length; i++) {
	        sb.append(string.charAt(getRandom(len-1)));
	    }
	    return sb.toString();
	}

	public static String getIdByUUId() {
        int first = new Random(10).nextInt(8) + 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return first + String.format("%015d", hashCodeV);
    }
	
	/**
	 * 生成房间号
	 * @param key
	 * @param date
	 * @return
	 */
	public static String generateRoomId(String key,Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(date);
		String random = getRandomString(6);
		return key+time+random;
	}
}
