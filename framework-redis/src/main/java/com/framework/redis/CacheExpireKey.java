package com.framework.redis;

import java.util.Map;

public interface CacheExpireKey {

    Map<String,Long> expireKeys();
}
