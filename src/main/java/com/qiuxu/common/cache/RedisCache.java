package com.qiuxu.common.cache;

import java.util.Collection;
import java.util.Map;


public interface RedisCache<K,V> {
	
	/**
	 * 指定缓存失效时间 
	 * @param key
	 * @param time
	 * @return
	 */
	boolean expire(K key,long time);
	
	/**
	 * 根据key 获取过期时间 
	 * @param key
	 * @return
	 */
	long getExpire(K key);
	
	/** 
     * 判断key是否存在 
     * @param key 键 
     * @return true 存在 false不存在 
     */  
     boolean hasKey(K key);
     
	 /** 
     * 在redis数据库中插入 key  和value 
     * @param key 
     * @param value 
     * @return 
     */  
    boolean set(K key,V value);  
    /** 
     * 在redis数据库中插入 key  和value 并且设置过期时间 
     * @param key 
     * @param value 
     * @param exp 过期时间 s 
     * @return 
     */  
    boolean set(K key, V value, int exp);  
    /** 
     * 根据key 去redis 中获取value 
     * @param key 
     * @return 
     */  
    V get(K key);  
    /** 
     * 删除redis库中的数据 
     * @param key 
     * @return 
     */  
    boolean remove(K key);  
    
    /** 
     * 删除缓存 
     * @param key 可以传一个值 或多个 
     */  
    void remove(K ... key);
    
    /** 
     * 删除redis库中的数据 
     * @param key 
     * @return 
     */  
    void remove(Collection<K> keys);  
    /** 
     * 设置哈希类型数据到redis 数据库 
     * @param cacheKey 可以看做一张表 
     * @param key   表字段 
     * @param value   
     * @return 
     */  
    boolean hset(K cacheKey, K hashKey, V value); 
    
    /** 
     * 递增 
     * @param key 键 
     * @param by 要增加几(大于0) 
     * @return 
     */  
    long incr(K key, long delta);
    
    /** 
     * 递减 
     * @param key 键 
     * @param by 要减少几(小于0) 
     * @return 
     */  
     long decr(K key, long delta);
	
    /** 
     * 获取哈希表数据类型的值 
     * @param cacheKey 
     * @param key 
     * @return 
     */  
    
    Object hget(K cacheKey,K hashKey);  
    
    /** 
     * 设置哈希类型数据到redis 数据库 
     * @param cacheKey 可以看做一张表 
     * @param key   表字段 
     * @param value   
     * @return 
     */  
    boolean hmset(K cacheKey, Map<K,V> map, long time); 
    /** 
     * 获取hashKey对应的所有键值 
     * @param cacheKey 
     * @return 
     */ 
    Map<K,V> hmget(K cacheKey);  
	

}
