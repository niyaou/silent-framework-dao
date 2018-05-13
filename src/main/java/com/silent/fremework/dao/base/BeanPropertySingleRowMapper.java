package com.silent.fremework.dao.base;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

/**
 * 根据JDBC查询返回单行数据Mapper
 * @author TanJin
 * @date 2017年6月13日
 */
public class BeanPropertySingleRowMapper<T> implements ResultSetExtractor<T> {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private Class<T> clazz;
    private Map<String, PropertyDescriptor> mappedFields;

    public BeanPropertySingleRowMapper(Class<T> clazz){
        initialize(clazz);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void initialize(Class<T> clazz){
        this.clazz = clazz;
		try {
			this.mappedFields = BeanPropertyDescriptorFactory.getInstance().getPropertyDescriptors(clazz);
		} catch (Exception e) {
			logger.error("",e);
		}
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        Class<T> clazz = getClazz();
        try {
            while(rs.next()){
                T t = BeanUtils.instantiate(clazz);
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(t);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for(int i = 1; i <= columnCount; i++){
                    String column = StringUtils.capitalize(JdbcUtils.lookupColumnName(rsmd, i));
                    PropertyDescriptor pd = this.mappedFields.get(column.replaceAll(" ", "").toLowerCase());
                    if (pd != null) {
                    	Object value = JdbcUtils.getResultSetValue(rs, i, pd.getPropertyType());
                        bw.setPropertyValue(pd.getName(),value);
                    }
                }
                return t;
            }
        } catch (IllegalArgumentException e) {
            logger.error("", e);
        }
        return null;
	}

}
