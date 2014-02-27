package com.minsub.utils;

public class JStringUtils {
	private JStringUtils(){}
	
	// 0~99의 숫자를 입력받아 0을 채운 String형태로 반환
	public static String retainNumber10(int number) {
		if (number < 10) {
			return "0" + number;
		} else if (number < 100) {
			return String.valueOf(number);
		} else {
			return null;
		}
	}
	
	public static String convertVertical(String str) {
		StringBuilder sp = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			sp.append(str.charAt(i));
			sp.append('\n');
		}
		return sp.toString();
	}
}
