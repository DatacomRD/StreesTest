package com.dtc.strees.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 以 yyyyMMddHHmmss 為基礎的序號，每秒最多產生 999999 個，
 * 如果超過就會重複（賭看看了...）。
 */
public class SerNoGenerator {
	private int count;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private DecimalFormat df = new DecimalFormat("000000");
	private long last;

	public SerNoGenerator() {
		last = getCurrentTimeWithSecondsPrecision();
		count = 1;
	}

	synchronized public String next() {
		long now = getCurrentTimeWithSecondsPrecision();

		if ((now - last) > 0) { //超過一秒了
			last = now;
			count = 1;
		}

		return sdf.format(new Date(now*1000)) + df.format(count++);
	}

	private long getCurrentTimeWithSecondsPrecision() {
		return System.currentTimeMillis() / 1000;
	}
}
