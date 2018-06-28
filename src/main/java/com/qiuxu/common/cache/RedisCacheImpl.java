package com.qiuxu.common.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;


public class RedisCacheImpl<K,V> implements RedisCache<K,V>{
	
	@Autowired
	private RedisTemplate<K, V> redisTemplate;
	
	/** 
     * 判断key是否存在 
     * @param key 键 
     * @return true 存在 false不存在 
     */ 
	@Override
	public boolean hasKey(K key){  
        try {  
            return redisTemplate.hasKey(key);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    } 
    
    /** 
     * 指定缓存失效时间 
     * @param key 键 
     * @param time 时间(秒) 
     * @return 
     */  
	@Override
    public boolean expire(K key,long time){  
        try {  
            if(time>0){  
                redisTemplate.expire(key, time, TimeUnit.SECONDS);  
            }  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 根据key 获取过期时间 
     * @param key 键 不能为null 
     * @return 时间(秒) 返回0代表为永久有效 
     */  
	@Override
    public long getExpire(K key){  
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);  
    }  
	
	@Override
	public boolean set(K key, V value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
	}

	@Override
	public boolean set(K key, V value, int exp) {
		try {
			if(exp > 0){
				redisTemplate.opsForValue().set(key, value, exp, TimeUnit.SECONDS);  	
			}else{
				set(key, value);
			}
			return true;  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public V get(K key) {
		return key==null?null:redisTemplate.opsForValue().get(key);  
	}

	@Override
	public boolean remove(K key) {
		if(hasKey(key)){
			redisTemplate.delete(key);
		}
		return true;
	}
	
	@Override
	public void remove(Collection<K> keys) {
		redisTemplate.delete(keys);
	}
	
	@Override
	public void remove(K ... key) {
		if(key!=null&&key.length>0){  
            if(key.length==1){  
            	remove(key[0]);
            }else{  
                remove(CollectionUtils.arrayToList(key));  
            }  
        }  
	}

	
	 /** 
     * 递增 
     * @param key 键 
     * @param by 要增加几(大于0) 
     * @return 
     */  
	@Override
    public long incr(K key, long delta){    
        if(delta<0){  
            throw new RuntimeException("递增因子必须大于0");  
        }  
        return redisTemplate.opsForValue().increment(key, delta);  
    }  
      
    /** 
     * 递减 
     * @param key 键 
     * @param by 要减少几(小于0) 
     * @return 
     */  
	@Override
    public long decr(K key, long delta){    
        if(delta<0){  
            throw new RuntimeException("递减因子必须大于0");  
        }  
        return redisTemplate.opsForValue().increment(key, -delta);    
    }    
      
    
	
	@Override
	public boolean hset(K cacheKey, K hashKey, V value) {
		redisTemplate.opsForHash().put(cacheKey, hashKey, value);
		return true;
	}

	@Override
	public Object hget(K cacheKey, K hashKey) {
		return redisTemplate.opsForHash().get(cacheKey, hashKey);
	}


	@Override
	public boolean hmset(K cacheKey,Map<K,V> map, long time) {
		 try {  
            redisTemplate.opsForHash().putAll(cacheKey, map);  
            if(time>0){  
                expire(cacheKey, time);  
            }  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
	}

	@Override
	public Map<K, V> hmget(K cacheKey) {
		return (Map<K, V>) redisTemplate.opsForHash().entries(cacheKey);  
	}

	

	
	

	
}
