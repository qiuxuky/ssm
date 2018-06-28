package com.qiuxu.common.persistence;

import java.io.Serializable;
import java.util.List;



import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.PageInfo;

/**
 * 通用泛型DAO基类
 * 
 * @author qiuxu
 * @param <T>
 */
public interface BaseDao<T> {

	/**
	 * 通过ID查询
	 * @param id
	 * @return
	 */
	public Object selectById(Serializable id);
	
	/**
	 * 查询单条记录
	 * @param entity
	 * @return
	 */
	public Object selectOne(@Param("item")Object obj);

	/**
	 * 查询记录集合
	 * @param entity
	 * @return
	 */
	public List<?> selectList(@Param("item")Object obj);
	
	/**
	 * 分页查询
	 * @param t
	 * @param page
	 * @return
	 */
	public List<T> selectPage(@Param("item")Object obj, @Param("page")PageInfo<T> page);

	/**
	 * 通用的保存方法
	 * @param <T>
	 * @param entity
	 */
	public int save(@Param("item")Object obj);
	
	/**
	 * 批量保存
	 * @param list
	 */
	public int batchSave(List<?> list);

	/**
	 * 通用的修改方法
	 * @param <T>
	 * @param entity
	 */
	public int update(@Param("item")Object obj);
	
	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	public int batchUpdate(List<?> list);

	/**
	 * 删除方法
	 * @param id
	 */
	public int delById(Serializable id);
	
	/**
	 * 批量删除
	 * @param list
	 * @return
	 */
	public int delList(List<?> list);

	/**
	 * 批量删除方法
	 * @param ids
	 */
	public int delArray(long[] ids);

	/**
	 * 统计查询
	 * @param <T>
	 * @param params 查询参数
	 * @return 总记录条数
	 */
	public int count(Object obj);

}