package com.silent.fremework.dao.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bean类Class操作、Field操作、Annotation操作
 * @author TanJin
 * @date 2017年6月13日
 */
public class ClassUtils {
	private static Map<Class<?>, Field[]> fieldCacheMap = new ConcurrentHashMap<Class<?>, Field[]>();	//Bean对应所有字段集合
	private static Map<Class<?>, List<Field>> dataSourceFieldListCacheMap = new ConcurrentHashMap<Class<?>, List<Field>>();	//Bean对应数据库字段列表
	private static Map<String, String> columnNameCacheMap = new ConcurrentHashMap<String, String>();	//字段对应的数据表列名
	private static Map<Class<?>, String> primaryKeyMap = new ConcurrentHashMap<Class<?>, String>();		//主键名Map
	private static Map<Class<?>, String> tableNameMap = new ConcurrentHashMap<Class<?>, String>();		//表名Map
	
	/**
	 * 判断字段是否为主键，true是  false不是
	 * @param 
	 * @return
	 * @date 2017年6月13日
	 */
	public static boolean isPrimaryKey(Field field) {
		Id id = field.getAnnotation(Id.class);
		return null != id;
	} 
	
	/**
	 * 获取class的所有field
	 * @param clazz
	 * @return
	 */
	public static Field[] getFields(Class<?> clazz){
		Field[] fields = fieldCacheMap.get(clazz);
		if(fields==null) {
			fields = clazz.getDeclaredFields();
			fieldCacheMap.put(clazz, fields);
		}
		return fields;
	}
	
	/**
	 * 获取class里面有 Id, Column 标注的字段，即所有数据表字段
	 * @param clazz
	 * @return
	 */
	public static List<Field> getDataSourceFieldList(Class<?> clazz) {
		List<Field> list = dataSourceFieldListCacheMap.get(clazz);
		if(list == null){
			Field[] fields = getFields(clazz);
			list = new ArrayList<Field>(fields.length);
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (column == null) {
					Id id = field.getAnnotation(Id.class);
					if(null == id) {
						continue;
					} else {
						list.add(field);
					}
				} else {
					list.add(field);
				}
			}
			dataSourceFieldListCacheMap.put(clazz, list);
		}	
		return list;
	}
	
	/**
	 * 获取field对应的数据表列名
	 * @param field
	 * @return
	 */
	public static String getFieldColumnName(Field field) {
		String columnName = columnNameCacheMap.get(field.toString());
		if(columnName == null){
			Column column = field.getAnnotation(Column.class);
			if(column != null){
				columnName = column.name();
				if (StringUtils.isEmpty(columnName)) {
					columnName = field.getName();
				}
			} else {
				Id id = field.getAnnotation(Id.class);
				if(id != null) {
					columnName = field.getName();
				}
			}
			
			if(columnName == null){
				columnName = "";
			}
			columnNameCacheMap.put(field.toString(), columnName);
		}
		return columnName;
	}
	
	/**
	 * 获取主键Field
	 * @param 
	 * @return
	 * @date 2017年6月13日
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) throws IOException {
		Field[] fields = getFields(clazz);
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if(null != id) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * 获取数据表对应的Bean中设置的主键名
	 * @param clazz 
	 * @throws IOException 
	 * @date 2017年6月13日
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz) throws IOException {
		String primaryKeyFieldName = primaryKeyMap.get(clazz);
		if(StringUtils.isNotEmpty(primaryKeyFieldName)) {
			return primaryKeyFieldName;
		}
		
		Field[] fields = getFields(clazz);
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if(null != id) {
				primaryKeyFieldName = field.getName();
			}
		}
		
		if(StringUtils.isEmpty(primaryKeyFieldName)) {
			throw new IOException(clazz + " not find @Id Annotation");
		}
		primaryKeyMap.put(clazz, primaryKeyFieldName);
		return primaryKeyFieldName;
	}
	
	/**
	 * 获取数据表对应的Bean中设置的表名
	 * @param clazz 
	 * @throws IOException 
	 * @date 2017年6月13日
	 */
	public static String getTableName(Class<?> clazz) throws IOException {
		Table table = clazz.getAnnotation(Table.class);
		if(null == table) {
			throw new IOException(clazz + " not find @Table Annotation");
		}
		String tableName = table.name();
		if(StringUtils.isEmpty(tableName)) {
			throw new IOException(clazz + " @Table Annotation not set tableName");
		}
		tableNameMap.put(clazz, tableName);
		return tableName;
	}
}
