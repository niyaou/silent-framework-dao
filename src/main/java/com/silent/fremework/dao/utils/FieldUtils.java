package com.silent.fremework.dao.utils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 字段数据操作
 * @author TanJin
 * @date 2017年6月14日
 */
public class FieldUtils {

	/**
	 * 获取指定Bean中的指定字段值
	 * @param beanObject bean对象信息
	 * @param field 字段
	 * @return
	 * @date 2017年6月14日
	 */
	public static String getFieldValue(Object beanObject, Field field) throws IOException {
		Object value = getField(beanObject, field);
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * 给指定Bean对象指定字段添值
	 * @param beanObject bean对象信息
	 * @param field 字段
	 * @param fieldValue 字段值
	 * @return
	 * @date 2017年6月14日
	 */
	public static void setFieldValue(Object beanObject, Field field, String fieldValue) throws IOException {
		DataUtils.setField(beanObject, field, fieldValue);
	}

	/**
	 * 获取指定Bean中的指定字段值
	 * @param beanObject bean对象信息
	 * @param field 字段
	 * @return
	 * @date 2017年6月14日
	 */
	public static Object getField(Object beanObject, Field field) throws IOException {
		return DataUtils.getField(beanObject, field);
	}
}
