package com.framework.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
public class RedisUtil {
	@SuppressWarnings("rawtypes")
	@Autowired
	private StringRedisTemplate redisTemplate;
	
    private static final String SETNX_EXPIRE_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then\n"  
            + "return redis.call('expire', KEYS[1], ARGV[2]);\n" + "end\n" + "return 0;";
	
    
    /**
     * 通过Key值删除
     * @param keys
     * @return
     */
    @SuppressWarnings({"unchecked"})
	public long del(final String... keys) {
		long result=(long) redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				 long result
						 = 0;
	                for (int i = 0; i < keys.length; i++) {
	                    result = connection.del(keys[i].getBytes());
	                }
	                return result;
			}
			
		});
		return result;
	}
    
    
	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			throw  e;
		}
		return result;
	}

	/**
	 * 设置位图
	 * @param key
	 * @param offset
	 * @param vaule
	 * @return
	 */
	public boolean setBit(final String key,long offset,boolean vaule) {
		boolean result = false;
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			operations.setBit(key,offset,vaule);
			result = true;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

//	public int countBit(){
//		try{
//			ValueOperations operations = redisTemplate.opsForValue();
//			operations
//		}catch (Exception e){
//			throw  e;
//		}
//	}
	/**
	 * 获取位图
	 * @param key
	 * @param offset
	 * @return
	 */
	public boolean getBit(final String key,long offset) {
		boolean result = false;
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			result=operations.getBit(key,offset);
		} catch (Exception e) {
			throw  e;
		}
		return result;
	}
	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 * @param expireTime 单位:秒
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean set(final String key, Object value, Long expireTime) {
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			operations.set(key, value, expireTime, TimeUnit.SECONDS);
			return  true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  批量添加键值对
	 * @param map
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean multiSet(Map<String,Object> map) {
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			operations.multiSet(map);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  批量获取键值对
	 * @param keyList key集合
	 * @return
	 */
	public List<String>  multiGet(List<String> keyList){
		try {
			ValueOperations operations = redisTemplate.opsForValue();
			return operations.multiGet(keyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断缓存中是否有对应的value
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 读取缓存,将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object getSet(final String key,Object value) {
		Object result = null;
		ValueOperations operations = redisTemplate.opsForValue();
		result = operations.getAndSet(key, value);
		return result;
	}
	
	/**
	 * 读取缓存
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"rawtypes"})
	public Object get(final String key) {
		Object result = null;
		ValueOperations operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}


	/**
	 * 删除对应的value
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}
	
	/**
	 * 缓存List
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean setListLeft(final String key, Object value) {
		boolean result = false;
		try {
			ListOperations operations = redisTemplate.opsForList();
			operations.leftPush(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 缓存List
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean setListRight(final String key, Object value) {
		boolean result = false;
		try {
			ListOperations operations = redisTemplate.opsForList();
			operations.rightPush(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取缓存
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object getListLeft(final String key) {
		Object result = null;
		try {
			ListOperations operations = redisTemplate.opsForList();
			result = operations.leftPop(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 读取缓存
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object getListRight(final String key) {
		Object result = null;
		try {
			ListOperations operations = redisTemplate.opsForList();
			result = operations.rightPop(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 缓存Hash
	 * @param key
	 * @param hashKay
	 * @param value
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean setHash(final String key, String hashKay,Object value) {
		boolean result = false;
		try {
			HashOperations operations = redisTemplate.opsForHash();
			operations.put(key, hashKay, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 缓存Hash
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public boolean setHash(final String key, Map<String,Object> value) {
		boolean result = false;
		try {
			HashOperations operations = redisTemplate.opsForHash();
			operations.putAll(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取缓存hash
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Map<String,Object> getHash(final String key) {
		Map<String,Object> result = null;
		try {
			HashOperations operations = redisTemplate.opsForHash();
			result = operations.entries(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取hash长度
	 * @param key
	 * @return
	 */
	public long getHashSize(final String key) {
		long result=0;
		try {
			HashOperations operations = redisTemplate.opsForHash();
			result = operations.size(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 判断缓存中是否有对应的value
	 * @param key
	 * @param hashKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean existsHash(final String key,final String hashKey) {
		return redisTemplate.opsForHash().hasKey(key, hashKey);
	}

	/**
	 * 删除缓存
	 * @param key
	 * @param
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public void deleteHash(final String key,final Object... hashKeys) {
		redisTemplate.opsForHash().delete(key, hashKeys);
	}
	
	/**
	 * 设置key并设置有效期
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public boolean setnxAndExpire(String key,Object value,int seconds){
		RedisScript<Long> script = new DefaultRedisScript<Long>(SETNX_EXPIRE_SCRIPT, Long.class);
		Long result = (Long)redisTemplate.execute(script,Collections.singletonList(key),value,seconds);
		return result>0?true:false;
	}

	/**
	 * 设置setNX
	 * @param key
	 * @param timeout	过期时间（秒）
	 * @return
	 */
	public boolean setNX(String key, int timeout) {
		Boolean notExists = redisTemplate.getConnectionFactory().getConnection().setNX(key.getBytes(), new byte[0]);
		Boolean expire = redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return notExists != null?notExists.booleanValue():false;
	}
	
	/**
	 * 按指定的值递增或递减
	 * @param key
	 * @param delta
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public Long incr(String key,long delta){
		return redisTemplate.opsForValue().increment(key, delta);
	}
	
	/**
	 * 按指定的值递增或递减
	 * @param key
	 * @param delta
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public Double incr(String key,double delta){
		return redisTemplate.opsForValue().increment(key, delta);
	}
	
	/**
	 * 返回键值有效期
	 * @param key
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public Long getExpire(String key){
		return redisTemplate.getExpire(key);
	}
	/**
	 * 更新key过期时间
	 * @param key 关键字
	 * @param timeout 有效期
	 * @param unit 时间单位
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public Boolean expire(String key,long timeout,TimeUnit unit){
		return redisTemplate.expire(key, timeout, unit);
	}

	/**
	 *  添加集合
	 * @param key
	 * @param values
	 */
	public long  sadd(String key,Object... values){
		try {
			SetOperations operations=redisTemplate.opsForSet();
			long result=operations.add(key,values);
			return  result;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  判断某个vaule是否存在Set
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean isMember(String key,Object value){
		try {
			SetOperations operations=redisTemplate.opsForSet();
			boolean result=operations.isMember(key,value);
			return  result;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  获取集合Set长度
	 * @param key
	 * @return
	 */
	public long getSetSize(String key){
		try {
			SetOperations operations=redisTemplate.opsForSet();
			long result=operations.size(key);
			return  result;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *  获取集合里key的成员
	 * @param key
	 * @return
	 */
	public Set getSetMembers(String key){
		try {
			SetOperations operations=redisTemplate.opsForSet();
			Set resultSet=operations.members(key);
			return  resultSet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 添加有序列表
	 * @param key
	 * @param vaule
	 * @param score
	 * @return
	 */
	public boolean zSetAdd(String key,Object vaule,double score){
		try {
			ZSetOperations operations=redisTemplate.opsForZSet();
			boolean result=operations.add(key,vaule,score);
			return  result;
		} catch (Exception e) {
			throw e;
		}
	}
}
