package com.partygo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static String getDateStr(Date d) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(d);
	}

}
