package com.qiuxu.common.persistence.impl;

import com.github.pagehelper.PageInfo;
import com.qiuxu.common.cache.RedisCache;
import com.qiuxu.common.persistence.BaseDao;
import com.qiuxu.common.persistence.BaseService;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public abstract class BaseServiceImpl<T extends Serializable> implements BaseService<T> {

	public abstract BaseDao<T> getBaseDao();
	
    @Override
    public Object selectById(Serializable id) {
        return getBaseDao().selectById(id);
    }

    @Override
    public Object selectOne(@Param("item")Object obj) {
        return getBaseDao().selectOne(obj);
    }

    @Override
    public List<?> selectList(@Param("item")Object obj) {
        return getBaseDao().selectList(obj);
    }

    @Override
    public PageInfo<T> selectPage(@Param("item")Object obj, @Param("page")PageInfo<T> page) {
        List<T> list = getBaseDao().selectPage(obj,page);
        return new PageInfo<T>(list);
    }

    @Override
    public int save(@Param("item")Object obj) {
        return getBaseDao().save(obj);
    }

    @Override
    public int batchSave(List<?> list) {
        return getBaseDao().batchSave(list);
    }

    @Override
    public int update(@Param("item")Object obj) {
        return getBaseDao().update(obj);
    }

    @Override
    public int batchUpdate(List<?> list) {
        return getBaseDao().batchUpdate(list);
    }

    @Override
    public int delById(Serializable id) {
        return getBaseDao().delById(id);
    }

    @Override
    public int delList(List<?> list) {
        return getBaseDao().delList(list);
    }

    @Override
    public int delArray(long[] ids) {
        return getBaseDao().delArray(ids);
    }

    @Override
    public int count(Object obj) {
        return getBaseDao().count(obj);
    }
}