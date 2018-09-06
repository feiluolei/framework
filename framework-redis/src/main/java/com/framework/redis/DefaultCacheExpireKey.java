package com.framework.redis;

import com.framework.redis.CacheExpireKey;

import java.util.Collections;
import java.util.Map;

public class DefaultCacheExpireKey implements CacheExpireKey {
	public Map<String,Long> expireKeys(){
		return Collections.emptyMap();
	};

}
