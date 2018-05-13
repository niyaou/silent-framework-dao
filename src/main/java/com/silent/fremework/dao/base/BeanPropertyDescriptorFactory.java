package com.silent.fremework.dao.base;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.BeansException;

import com.silent.fremework.dao.utils.ClassUtils;
import com.silent.fremework.dao.utils.StringUtils;

/**
 * 获取指定Bean的PropertyDescriptor<br>
 * 通过识别Bean中的@Column注解及@Id注解获取数据表字段，若@Column中name有值则使用name值，否则使用字段名
 * @author TanJin
 * @date 2017年6月12日
 */
public class BeanPropertyDescriptorFactory {

	private static BeanPropertyDescriptorFactory instance = new BeanPropertyDescriptorFactory();
	private BeanPropertyDescriptorFactory() {}
	public static BeanPropertyDescriptorFactory getInstance() {
		return instance;
	}
	
	private Map<Class<?>, Map<String, PropertyDescriptor>> map = new HashMap<Class<?>, Map<String, PropertyDescriptor>>();
	
	public Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> clazz) throws BeansException, IOException, IntrospectionException {
		Map<String, PropertyDescriptor> pd = map.get(clazz);
		if(null != pd && pd.size() > 0) {
			return pd;
		}
	
		List<Field> list = ClassUtils.getDataSourceFieldList(clazz);
		if(list.size() <= 0) {
			throw new IOException(clazz + " not find @Id or @Column Annotation");
		}
		
		Map<String, PropertyDescriptor> descriptorMap = new HashMap<String, PropertyDescriptor>();
		for(int i = 0; i < list.size(); i++) {
			Field field = list.get(i);
			String fieldName = "";
			Column column = field.getAnnotation(Column.class);
			if(null != column) {
				fieldName = column.name();
				if(StringUtils.isEmpty(fieldName)) {
					fieldName = field.getName();
				}
				
			} else {
				Id id = field.getAnnotation(Id.class);
				if(null != id) {
					fieldName = field.getName();
					
				}
			}
			if(StringUtils.isNotEmpty(fieldName)) {
				PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
				descriptorMap.put(fieldName, descriptor);
			}
		}
		
		map.put(clazz, descriptorMap);
		return descriptorMap;
	}
}
