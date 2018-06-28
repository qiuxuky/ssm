package com.qiuxu.ssm.dao;

import com.qiuxu.common.annotation.MyBatisDao;
import com.qiuxu.common.persistence.BaseDao;
import com.qiuxu.ssm.domain.User;

@MyBatisDao
public interface UserDao extends BaseDao<User> {
}