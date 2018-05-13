package com.silent.fremework.dao.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.silent.fremework.dao.utils.ClassUtils;
import com.silent.fremework.dao.utils.FieldUtils;
import com.silent.fremework.dao.utils.StringUtils;

/**
 * 数据库操作封装类
 * @author TanJin
 * @date 2017年6月13日
 */
@Component
public class HandpetDefaultDao {
	protected Logger logger = LoggerFactory.getLogger(HandpetDefaultDao.class);

	@Autowired(required = false)
	protected JdbcTemplate jdbcTemplate;
	
	/**
	 * 获取数据
	 * @Title query
	 * @Description
	 * @param
	 * @return T
	 * @date 2017年8月6日
	 */
	public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
		return jdbcTemplate.query(sql, args, rse);
	}
	
	/**
	 * 根据主键获取值
	 * @param primaryKeyValue 主键值
	 * @param clazz 对象Class
	 * @return
	 * @date 2017年6月13日
	 */
	public <T> T queryByPrimaryKey(Object primaryKeyValue, Class<T> clazz) throws IOException {
		StringBuilder where = new StringBuilder();
		where.append(ClassUtils.getPrimaryKeyFieldName(clazz)).append("= ?");
		return query(where.toString(), new Object[]{primaryKeyValue}, clazz);
	}
	
	/**
	 * 根据where条件获取值
	 * @param where where条件
	 * @param params 参数值
	 * @param clazz 对象Class
	 * @return
	 * @date 2017年6月13日
	 */
	public <T> T query(String where, Object[] params, Class<T> clazz) throws IOException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(ClassUtils.getTableName(clazz));
		if(StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		logger.debug("[HandpetDefaultDao] [query] [SQL:{}]", sql.toString());
		
		T t = jdbcTemplate.query(sql.toString(), params, new BeanPropertySingleRowMapper<T>(clazz));
		return t;
	}
	
	/**
	 * 获取所有数据
	 * @param clazz 对象Class
	 * @date 2017年6月13日
	 */
	public <T> List<T> queryAll(Class<T> clazz) throws IOException {
		return queryList(null, null, null, clazz);
	}
	
	/**
	 * 根据where条件获取值列表
	 * @param where where条件
	 * @param order 排序条件
	 * @param params 参数值
	 * @param clazz 对象Class
	 * @date 2017年6月13日
	 */
	public <T> List<T> queryList(String where, String order, Object[] params, Class<T> clazz) throws IOException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(ClassUtils.getTableName(clazz));
		if(StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		if(StringUtils.isNotEmpty(order)) {
			sql.append(" ORDER BY ").append(order);
		}
		logger.debug("[HandpetDefaultDao] [queryList] [SQL:{}]", sql.toString());
		List<T> list = jdbcTemplate.query(sql.toString(), params, new BeanPropertyRowsMapper<T>(clazz));
		return list;
	}
	
	/**
	 * 根据where条件获取值列表
	 * @param where where条件
	 * @param order 排序条件
	 * @param params 参数值
	 * @param start limit 开始值
	 * @param end limit 结束值
	 * @param clazz 对象Class
	 * @return
	 * @date 2017年6月13日
	 */
	public <T> List<T> queryList(String where, String order, Object[] params, int start, int end, Class<T> clazz) throws IOException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(ClassUtils.getTableName(clazz));
		if(StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		if(StringUtils.isNotEmpty(order)) {
			sql.append(" ORDER BY ").append(order);
		}
		sql.append(" LIMIT ").append(start-1).append(",").append(end-start);
		
		logger.debug("[HandpetDefaultDao] [queryList] [SQL:{}]", sql.toString());
		
		List<T> list = jdbcTemplate.query(sql.toString(), params, new BeanPropertyRowsMapper<T>(clazz));
		return list;
	}
	
	/**
	 * 获取指定表数据总数
	 * @param clazz 对象Class
	 * @date 2017年6月13日
	 */
	public <T> int queryCount(Class<T> clazz) throws IOException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*)").append(" ").append(ClassUtils.getTableName(clazz));
		logger.debug("[HandpetDefaultDao] [queryCount] [SQL:{}]", sql.toString());
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql.toString());
		while(rowSet.next()) {
			int count = rowSet.getInt(0);
			return count;
		}
		return 0;
	}

	/**
	 * 根据where条件获取指定表数据总数
	 * @param where where条件
	 * @param params 参数值
	 * @param clazz 对象Class
	 * @return
	 * @date 2017年6月13日
	 */
	public <T> int queryCount(String where, Object[] params, Class<T> clazz) throws IOException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1)").append(" ").append(ClassUtils.getTableName(clazz));
		if(StringUtils.isNotEmpty(where)) {
			sql.append(" WHERE ").append(where);
		}
		logger.debug("[HandpetDefaultDao] [queryCount] [SQL:{}]", sql.toString());
		
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql.toString(), params);
		while(rowSet.next()) {
			int count = rowSet.getInt(0);
			return count;
		}
		return 0;
	}
	
	/**
	 * 修改或新增数据；若数据不存在则新增
	 * @param beanObject Bean值信息
	 * @param clazz
	 * @date 2017年6月13日
	 */
	public <T> boolean updateOrInsert(Object beanObject, Class<T> clazz, boolean isUpdateTime) throws IOException {
		//获取主键的值
		Object primaryKeyValue = FieldUtils.getField(beanObject, ClassUtils.getPrimaryKeyField(clazz));
		T bean = queryByPrimaryKey(primaryKeyValue, clazz);
		boolean result = false;
		if(null == bean) {
			result = insert(beanObject, clazz, isUpdateTime) > 0;
		} else {
			result = update(beanObject, clazz, isUpdateTime) > 0;
		}
		return result;
	}
	
	/**
	 * 根据主键删除指定数据
	 * @param object Bean
	 * @param clazz
	 * @return
	 * @date 2017年6月13日
	 */
	public boolean deleteByPrimaryKey(Object primaryKeyValue, Class<?> clazz) throws IOException {
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(ClassUtils.getTableName(clazz));
		sql.append(" where ").append(ClassUtils.getPrimaryKeyFieldName(clazz)).append(" = ?");
		logger.debug("[HandpetDefaultDao] [deleteByPrimaryKey] [SQL:{}]", sql.toString());
		int count = jdbcTemplate.update(sql.toString(), primaryKeyValue);
		return count > 0;
	}
	
	/**
	 * 插入数据，仅支持插入单条数据
	 * @param beanObject Bean
	 * @return Bean对应的Class
	 * @date 2017年6月13日
	 */
	public int insert(Object beanObject, Class<?> clazz, boolean isUpdateTime) throws IOException {
		List<Object> paramList = new ArrayList<Object>();
		String insertSql = getInsertSql(beanObject, clazz, paramList, isUpdateTime);
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
		sqlBuilder.append(ClassUtils.getTableName(clazz));
		sqlBuilder.append(insertSql);
		return jdbcTemplate.update(sqlBuilder.toString(), paramList.toArray());
	}
	
	/**
	 * 根据主键更新指定表数据，注意：该方法只进行全量更新，即所有字段更新， 所以使用前需将字段值都保存至Bean中
	 * @param beanObject Bean
	 * @param Bean对应的Class
	 * @date 2017年6月13日
	 */
	public int update(Object beanObject, Class<?> clazz, boolean isUpdateTime) throws IOException {
		List<Object> paramList = new ArrayList<Object>();
		String updateSql = getUpdateSql(beanObject, clazz, paramList, isUpdateTime);
		StringBuilder sqlBuilder = new StringBuilder("update ");
		sqlBuilder.append(ClassUtils.getTableName(clazz));
		sqlBuilder.append(" set ").append(updateSql);
		sqlBuilder.append(" where ").append(ClassUtils.getPrimaryKeyFieldName(clazz)).append(" = ? ");
		
		String primaryKeyValue = FieldUtils.getFieldValue(beanObject, ClassUtils.getPrimaryKeyField(clazz));
		paramList.add(primaryKeyValue);
		return jdbcTemplate.update(sqlBuilder.toString(), paramList.toArray());
	}
	
	/**
	 * 生成Insert语句
	 * @date 2017年6月14日
	 */
	private String getInsertSql(Object beanObject, Class<?> clazz, List<Object> paramList, boolean isUpdateTime) throws IOException {
		List<Field> fields = ClassUtils.getDataSourceFieldList(clazz);
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		boolean first = true;
		for (Field field : fields) {
			if(ClassUtils.isPrimaryKey(field)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				columnBuilder.append(",");
				valueBuilder.append(",");
			}
			String columnName = ClassUtils.getFieldColumnName(field);
			columnBuilder.append(columnName);

			valueBuilder.append("?");
			Object param = FieldUtils.getField(beanObject, field);
			if(isUpdateTime && "time".equals(field.getName())) {
				param = System.currentTimeMillis();
			}
			paramList.add(param);
		}
		return "(" + columnBuilder.toString() + ") values (" + valueBuilder.toString() + ")";
	}
	
	/**
	 * 生成Update语句
	 * @date 2017年6月14日
	 */
	private String getUpdateSql(Object beanObject, Class<?> clazz, List<Object> paramList, boolean isUpdateTime) throws IOException {
		List<Field> fields = ClassUtils.getDataSourceFieldList(clazz);
		StringBuilder columnBuilder = new StringBuilder();
		boolean first = true;
		for (Field field : fields) {
			if(ClassUtils.isPrimaryKey(field)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				columnBuilder.append(",");
			}
			String columnName = ClassUtils.getFieldColumnName(field);
			columnBuilder.append(columnName).append("=?");
			Object param = FieldUtils.getField(beanObject, field);
			if(isUpdateTime && "time".equals(field.getName())) {
				param = System.currentTimeMillis();
			}
			paramList.add(param);
		}
		return columnBuilder.toString();
	}
}
