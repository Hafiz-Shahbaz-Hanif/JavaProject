package com.DC.utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

public class RedisUtility {

    private ReadConfig readConfig;
    private Logger logger;
    private String username;
    private String password;
    private String host;
    private String port;


    public RedisUtility() {
        logger = Logger.getLogger(RedisUtility.class);
        PropertyConfigurator.configure("log4j.properties");
        readConfig = ReadConfig.getInstance();
        host = readConfig.getRedisHost();
        port = readConfig.getRedisPort();
        username = readConfig.getRedisUsername();
        password = readConfig.getRedisPassword();
    }

    public JedisPooled connectToRedis() {
        HostAndPort address = new HostAndPort(host, Integer.parseInt(port));

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(true)
                .user(username)
                .password(password)
                .build();

        return new JedisPooled(address, config);
    }

    public String getRedisCache(String key){
        JedisPooled jedis = connectToRedis();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    public JSONObject getRedisCacheJson(String key){
        String caches = getRedisCache(key);
        return new JSONObject(caches);
    }

    public void clearRedisCache(String key){
        JedisPooled jedis = connectToRedis();
        jedis.del(key);
        jedis.close();
    }

}