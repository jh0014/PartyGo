package com.partygo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UuidUtil {

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
	
	public static String generateRoomId(String openid,Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(date); 
		return openid+"_"+time;
	}
}
