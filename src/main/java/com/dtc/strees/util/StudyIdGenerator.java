package com.dtc.strees.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StudyId 產生器
 * <p>
 * 規格：只允許有數字及小數點，長度不限
 * <p>
 * 使用 IP.yyyyMMdd.HHmmss.SSS.COUNT 來產生
 * <p>
 * 可在多執行緒、多台實體機器產生不重複的 id，
 * 但同一台機器用兩個 JVM 執行時就會重複。
 */
public class StudyIdGenerator {
	private static String HOST_IP;
	static {
		try {
			HOST_IP = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			HOST_IP = "127.0.0.1";
		}
	}

	private static int count;
	private static long last;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");
	private static DecimalFormat df = new DecimalFormat("#");

	synchronized public static String next() {
		long now = System.currentTimeMillis();

		if ((now - last) > 0) {
			last = now;
			count = 1;
		}

		return HOST_IP + "." + sdf.format(new Date(now)) + "." +  df.format(count++);
	}
}
