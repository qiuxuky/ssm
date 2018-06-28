package com.qiuxu.ssm.service.impl;

import java.io.Serializable;

import com.qiuxu.common.cache.RedisCache;
import com.qiuxu.common.persistence.BaseDao;
import com.qiuxu.common.persistence.impl.BaseServiceImpl;
import com.qiuxu.ssm.dao.UserDao;
import com.qiuxu.ssm.domain.User;
import com.qiuxu.ssm.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RedisCache<String,Object> redisCache;

    @Override
    public BaseDao<User> getBaseDao() {
        return this.userDao;
    }
	
}