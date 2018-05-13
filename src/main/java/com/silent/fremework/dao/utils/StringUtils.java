package com.silent.fremework.dao.utils;

/**
 * 字符串操作工具类
 * @author TanJin
 */
public class StringUtils {

	public static boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}
	
	public static boolean isNotEmpty(String string){
		return string != null && !string.isEmpty();
	}

	public static boolean isTrimEmpty(String str){
		return str == null || str.length() == 0 || str.trim().isEmpty();
	}
	
}
