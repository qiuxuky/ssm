package com.qiuxu.common.persistence;

import com.github.pagehelper.PageInfo;
import java.io.Serializable;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseService<T extends Serializable> {
    public Object selectById(Serializable id);

    public Object selectOne(@Param("item")Object obj);

    public List<?> selectList(@Param("item")Object obj);

    public PageInfo<T> selectPage(@Param("item")Object obj, @Param("page")PageInfo<T> page);

    public int save(@Param("item")Object obj);

    public int batchSave(List<?> list);

    public int update(@Param("item")Object obj);

    public int batchUpdate(List<?> list);

    public int delById(Serializable id);

    public int delList(List<?> list);

    public int delArray(long[] ids);

    public int count(Object obj);
}