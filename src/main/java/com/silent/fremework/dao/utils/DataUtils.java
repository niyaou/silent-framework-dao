package com.silent.fremework.dao.utils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 数据操作
 * @date 2017年6月14日
 */
public class DataUtils {
	/**
	 * 对象转字符串
	 * 
	 * @param value
	 * @return
	 */
	private static Object objectToValue(Object value) {
		if (value == null) {
			return null;
		}
		Class<?> c = value.getClass();
		if (c == byte[].class) {
			return Base64.encodeToString((byte[]) value);
		} else if (Enum.class.isAssignableFrom(c)) {
			return ((Enum<?>) value).name();
		} else {
			return value;
		}
	}

	/**
	 * 字符串转对象
	 * 
	 * @param object
	 * @param field
	 * @param value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object valueToObject(Field field, Object value) {
		if (field == null || value == null) {
			return null;
		}
		if (value.getClass() != String.class) {
			return value;
		}
		Class<?> c = field.getType();
		try {
			String setValue = (String) value;
			if (c == String.class) {
				value = setValue;
			} else if (c == byte[].class) {
				setValue = setValue.replaceAll("\r", "");
				setValue = setValue.replaceAll("\n", "");
				value = Base64.decode(setValue);
			} else if (c == short.class) {
				value = new Short(setValue);
			} else if (c == int.class) {
				value = new Integer(setValue);
			} else if (c == long.class) {
				value = new Long(setValue);
			} else if (c == boolean.class) {
				value = Boolean.valueOf(setValue);
			} else if (c == float.class) {
				value = new Float(setValue);
			} else if (c == double.class) {
				value = new Double(setValue);
			} else if (c == byte.class) {
				value = new Byte(setValue);
			} else if (Enum.class.isAssignableFrom(c)) {
				value = Enum.valueOf((Class<Enum>) c, setValue);
			} else {
				value = setValue;
			}
		} catch (NumberFormatException e) {
			e=null;
		}
		return value;
	}

	/**
	 * 获取变量
	 * @param beanObject
	 * @param field
	 * @return
	 * @throws IOException 
	 */
	public static Object getField(Object beanObject, Field field) throws IOException {
		try {
			field.setAccessible(true);
			Object value = field.get(beanObject);
			return DataUtils.objectToValue(value);
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * 存入变量
	 * 
	 * @param beanObject
	 * @param field
	 * @param fieldValue
	 * @throws IOException 
	 */
	public static boolean setField(Object beanObject, Field field, Object fieldValue) throws IOException {
		try {
			fieldValue = DataUtils.valueToObject(field, fieldValue);
			field.setAccessible(true);
			field.set(beanObject, fieldValue);
			return true;
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException(e.getMessage());
		}
	}
}