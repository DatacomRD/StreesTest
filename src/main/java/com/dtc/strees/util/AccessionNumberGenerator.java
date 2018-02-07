package com.dtc.strees.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * AccessionNumber 產生器
 * <p>
 * 規格：15 字元長度的英數
 * <p>
 * 使用 IP + 時間及計數，然後用編碼來縮減字串長度來避免重複
 * <p>
 * 可在多執行緒、多台實體機器產生不重複的 id，
 * 但同一台機器用兩個 JVM 執行時就會重複。
 */
public class AccessionNumberGenerator {
	private static String letters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static char[] letterArray = letters.toCharArray();
	private static int length = letterArray.length;
	private static String HOST_IP;
	private static String _BASE;

	static {
		try {
			HOST_IP = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			HOST_IP = "127.0.0.1";
		}

		_BASE = ipTrans(HOST_IP);
	}

	private static int count;
	private static long last;

	synchronized public static String next() {
		long now = System.currentTimeMillis();

		if ((now - last) > 0) {
			last = now;
			count = 1;
		}

		return _BASE + encode(now) + encode(count++);
	}

	private static String encode(long v) {
		StringBuilder sb = new StringBuilder();

		do {
			int index = (int)(v % length);
			sb.append(letterArray[index]);
			v = v / length;
		} while (v >= length);

		if (v > 0) {
			int index = (int)v;
			sb.append(letterArray[index]);
		}

		return sb.toString();
	}

	private static String ipTrans(String ip) {
		String[] t = ip.split("\\.");
		int v = Integer.parseInt(t[3]) * 16777216 +
			Integer.parseInt(t[2]) * 65536 +
			Integer.parseInt(t[1]) * 256 +
			Integer.parseInt(t[0]);
		return encode(v);
	}
}
