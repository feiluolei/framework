package com.framework.redis;

import com.RedisApplication;
import com.alibaba.fastjson.JSON;
import org.assertj.core.internal.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

import javax.management.StringValueExp;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Component
public class TestRedisApplication {
    private static Logger logger = LoggerFactory.getLogger(TestRedisApplication.class);
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final long USER_ID_PREXI = 100000;
    private static final String CURRENT_DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    @Test
    public void testSetString() {
        redisUtil.set("name", "zhangsan");

    }

    @Test
    public void testGetString() {
        String name = (String) redisUtil.get("name");
        logger.info("name:" + name);
    }

    @Test
    public void testMultiSet() {
        Map<String, Object> map = new HashMap<>();
        map.put("name1", "test1");
        map.put("name2", "test2");
        map.put("name3", "test3");
        boolean result = redisUtil.multiSet(map);
        logger.info("result:" + result);
    }

    @Test
    public void testMultiGet() {
        List<String> keyList = new ArrayList<>();
        keyList.add("name1");
        keyList.add("name2");
        keyList.add("name3");
        keyList = redisUtil.multiGet(keyList);
        logger.info(JSON.toJSONString(keyList));
    }

    @Test
    public void testSetNx() throws InterruptedException {
        boolean result = redisUtil.setNX("onlyKey", 5000);
        logger.info("result1:" + result);
        result = redisUtil.setNX("onlyKey", 5000);
        logger.info("result2:" + result);
        Thread.currentThread().sleep(5000);
        result = redisUtil.set("onlyKey", 5000);
        logger.info("result3:" + result);
    }

    @Test
    public void testSetListLeft() {
        for (int i = 0; i < 10; i++) {
            redisUtil.setListLeft("listLeft" + i, i);
        }
    }

    @Test
    public void testGetListRight() {
        for (int i = 0; i < 10; i++) {
            logger.info(String.valueOf(redisUtil.getListRight("listLeft" + i)));
        }
    }

    @Test
    public void testSetListRight() {
        for (int i = 0; i < 10; i++) {
            logger.info(String.valueOf(redisUtil.setListRight("listRight" + i, "listRight" + i)));
        }
    }

    @Test
    public void testGetListLeft() {
        for (int i = 0; i < 10; i++) {
            logger.info(String.valueOf(redisUtil.getListLeft("listRight" + i)));
        }
    }

    @Test
    public void testSetHash() {
        String key = "person";
        Map<String, Object> value = new HashMap<>();
        value.put("name", "test");
        value.put("age", 20);
        value.put("sex", "man");
        boolean result = redisUtil.setHash(key, value);
        logger.info("result:" + result);
    }

    @Test
    public void testgetHashSize() {
        long result = redisUtil.getHashSize("person");
        logger.info("result:" + result);
    }

    @Test
    public void testGetHash() {
        Map<String, Object> resultMap = redisUtil.getHash("person");
        logger.info("result:" + JSON.toJSONString(resultMap));
    }

    @Test
    public void testSadd() {
        long result = redisUtil.sadd("books", "python", "java", "golang", "java", "C++", "scala");
        logger.info("result:" + result);
    }

    @Test
    public void testIsMember() {
        boolean result = redisUtil.isMember("books", "java");
        logger.info("result:" + result);
        result = redisUtil.isMember("books", "swift");
        logger.info("result:" + result);
    }

    @Test
    public void testGetSetMembers() {
        Set setResult = redisUtil.getSetMembers("books");

        for (Iterator iterator = setResult.iterator(); iterator.hasNext(); ) {
            String result = (String) iterator.next();
            logger.info("result:" + result);
        }

    }

    @Test
    public void testZSetAdd() {
        boolean result = redisUtil.zSetAdd("books", "java", 8.9);
        logger.info("result:" + result);
        result = redisUtil.zSetAdd("books", "java", 9.2);
        logger.info("result:" + result);
        result = redisUtil.zSetAdd("books", "python", 9.3);
        logger.info("result:" + result);
        result = redisUtil.zSetAdd("books", "go", 9.0);
        logger.info("result:" + result);
        result = redisUtil.zSetAdd("books", "c", 8.8);
        logger.info("result:" + result);
    }

    @Test
    /**
     * 利用redis位图，记录每月的活跃用户
     *
     */
    public void testSetBit() {
        Date currentTime = new Date();
        String currentStr = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
        String key = "userLogin_" + currentStr;
        for (long i = 1; i <= 10; i++) {
            long userId = 8 * (long) (Math.random() * USER_ID_PREXI);
            boolean result = redisTemplate.opsForValue().setBit(key, userId, true);
            logger.info("key:" + key + ";userId:" + userId + ";result:" + result);
        }
    }

    @Test
    public void testGetBitSet() {
        String key = "userLogin_2018-09-05";
        String resultStr = redisTemplate.opsForValue().get(key);
        byte[] bytes = SafeEncoder.encode(resultStr);
        logger.info("bytes length:" + bytes.length);
        if (bytes != null && bytes.length > 0) {
            BitSet bitSet = byteArray2BitSet(bytes);
            BitSet newBitSet = new BitSet();
            for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
                boolean result = redisTemplate.opsForValue().getBit(key, i);
                logger.info("userId:" + i + ";vaue:" + result);
                if (result) {
                    newBitSet.set(i, true);
                }
            }
            logger.info("newBigSet cardinaltity:" + newBitSet.cardinality());
            for (int i = newBitSet.nextSetBit(0); i >= 0; i = newBitSet.nextSetBit(i + 1)) {
                boolean result = redisTemplate.opsForValue().getBit(key, i);
                logger.info("userId:" + i + ";vaue:" + result);
            }
        }
    }

    /**
     * 将ByteArray对象转化为BitSet
     *
     * @param bytes
     * @return
     */
    public static BitSet byteArray2BitSet(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1 ? true : false);
            }
        }
        return bitSet;
    }

    @Test
    public void testGetBit() {
        String key = "userLogin_2018-09-05";
        long[] userIds = new long[]{1027787465, 1334685201, 1841933886, 1278210630, 1080260678, 1416831009, 1757577916, 1242291442, 1351308704, 1931234385};
        for (long userId : userIds) {
            boolean result = redisTemplate.opsForValue().getBit(key, userId);
            logger.info("userId:" + userId + ";vaue:" + result);
        }
    }

    /**
     * HyperLogLog--pfAdd
     */
    @Test
    public void testPfAdd() {//对应的redis命令
        HyperLogLogOperations operations = redisTemplate.opsForHyperLogLog();
        String key = "total_uv_" + CURRENT_DATE;
        for (int i = 0; i < 1000; i++) {
            String userId = UUID.randomUUID().toString().replace("-", "");
            operations.add(key, userId);
            logger.info("key:" + key + ";userId:" + userId);
        }
    }

    /**
     * HyperLogLog--pfCount
     */
    @Test
    public void testPfCount() {
        HyperLogLogOperations operations = redisTemplate.opsForHyperLogLog();
        String key = "total_uv_" + CURRENT_DATE;
        long result = operations.size(key);
        logger.info("key:" + key + ";count:" + result);
    }

    /**
     * geoadd 指令携带集合名称以及多个经纬度名称三元组，注意这里可以加入多个三元组
     */
    @Test
    public void testGeoAdd() {
        String key = "company";
        GeoOperations operations = redisTemplate.opsForGeo();
        operations.add(key, new Point(116.48105, 39.996794), "juejin");
        operations.add(key, new Point(116.514203, 39.905409), "ireader");
        operations.add(key, new Point(116.489033, 40.007669), "meituan");
        operations.add(key, new Point(116.562108, 39.996794), "jd ");
    }

    /**
     * geodist 指令可以用来计算两个元素之间的距离，携带集合名称、2 个名称和距离单位
     */
    @Test
    public void testgGeodist() {
        String key = "company";
        GeoOperations operations = redisTemplate.opsForGeo();

        Distance distance = operations.distance(key, "juejin", "ireader");

        logger.info("juejin and ireader distance:" + distance.getValue() + ";metric:" + distance.getMetric());
    }
    @Test
    public void testgeoPos(){
        String key = "company";
        GeoOperations operations = redisTemplate.opsForGeo();
        List<Point> points=operations.position(key,"juejin","ireader");
        for (Point point:points) {
            logger.info("point x:"+point.getX()+"point y:"+point.getY());
        }
    }
    @Test
    public void test(){

    }
}


