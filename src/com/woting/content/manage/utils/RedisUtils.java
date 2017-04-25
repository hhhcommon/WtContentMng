package com.woting.content.manage.utils;

import javax.servlet.ServletContext;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
	private static JedisPool jedisPool = getPool();

	public static JedisPool getPool() {
		JedisPool pool = null;
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(1000);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(5);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(1000 * 100);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);
			pool = new JedisPool(config, "127.0.0.1", 6379);
		}
		return pool;
	}

	/**
	 * 释放jedis客户端
	 * 
	 * @param jedis
	 */
	@SuppressWarnings("deprecation")
	private static void release(Jedis jedis) {
		if(jedis!=null)
			jedisPool.returnResource(jedis);
	}
	
	public static void addPhoneCheckInfo(String phonenum, String checknum){
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set("phoneCheckInfo_"+phonenum, System.currentTimeMillis()+"="+checknum);
		} catch (Exception e) {} finally {
			release(jedis);
		}
	}
	
	public static String getPhoneCheckInfo(String phonenum){
		Jedis jedis = jedisPool.getResource();
		String checkinfo = "";
		try {
			checkinfo = jedis.get("phoneCheckInfo_"+phonenum);
		} catch (Exception e) {} finally {
			release(jedis);
		}
		return checkinfo;
	}
	
	public static void multiPhoneCheckInfo(String phonenum) {
		Jedis jedis = jedisPool.getResource();
		jedis.del("phoneCheckInfo_"+phonenum);
		release(jedis);
	}
	
	public static boolean setString (String connectionFactory, int redisDB, String key, String value, long expireTime) {
		RedisOperService redis = null;
		try {
			if (connectionFactory!=null) {
				ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
		        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
		            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean(connectionFactory);
		            redis = new RedisOperService(js, redisDB);
		            if (expireTime==0) return redis.set(key, value);
		            else return redis.set(key, value, expireTime);
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (redis!=null) try {redis.close();redis=null;} catch(Exception e) {redis=null;} finally {redis=null;};
		}
		return false;
	}
	
	public static boolean setString (String connectionFactory, int redisDB, String key, String value) {
		RedisOperService redis = null;
		try {
			if (connectionFactory!=null) {
				ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
		        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
		            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean(connectionFactory);
		            redis = new RedisOperService(js, redisDB);
		            return redis.set(key, value);
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (redis!=null) try {redis.close();redis=null;} catch(Exception e) {redis=null;} finally {redis=null;};
		}
		return false;
	}
	
	public static boolean pExpire(String connectionFactory, int redisDB, String key, long expireTime) {
		RedisOperService redis = null;
		try {
			if (connectionFactory!=null) {
				ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
		        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
		            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean(connectionFactory);
		            redis = new RedisOperService(js, redisDB);
		            return redis.pExpire(key, expireTime);
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (redis!=null) try {redis.close();redis=null;} catch(Exception e) {redis=null;} finally {redis=null;};
		}
		return false;
	}
}
