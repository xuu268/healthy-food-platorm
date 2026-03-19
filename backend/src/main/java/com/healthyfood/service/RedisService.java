package com.healthyfood.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis服务 - 封装Redis常用操作
 */
@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set(key, value);
        } catch (Exception e) {
            log.error("Redis设置缓存失败: key={}", key, e);
        }
    }
    
    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis设置缓存失败: key={}, timeout={}", key, timeout, e);
        }
    }
    
    /**
     * 设置缓存（带过期时间，单位：秒）
     */
    public void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            return ops.get(key);
        } catch (Exception e) {
            log.error("Redis获取缓存失败: key={}", key, e);
            return null;
        }
    }
    
    /**
     * 获取字符串缓存
     */
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 获取整数缓存
     */
    public Integer getInt(String key) {
        String value = getString(key);
        return value != null ? Integer.parseInt(value) : null;
    }
    
    /**
     * 获取长整型缓存
     */
    public Long getLong(String key) {
        String value = getString(key);
        return value != null ? Long.parseLong(value) : null;
    }
    
    /**
     * 获取布尔缓存
     */
    public Boolean getBoolean(String key) {
        String value = getString(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }
    
    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis删除缓存失败: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 批量删除缓存
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Redis批量删除缓存失败", e);
            return 0;
        }
    }
    
    /**
     * 删除匹配模式的缓存
     */
    public long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                return delete(keys);
            }
            return 0;
        } catch (Exception e) {
            log.error("Redis按模式删除缓存失败: pattern={}", pattern, e);
            return 0;
        }
    }
    
    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis设置过期时间失败: key={}, timeout={}", key, timeout, e);
            return false;
        }
    }
    
    /**
     * 设置过期时间（单位：秒）
     */
    public boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取过期时间
     */
    public long getExpire(String key, TimeUnit unit) {
        try {
            Long expire = redisTemplate.getExpire(key, unit);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败: key={}", key, e);
            return -2;
        }
    }
    
    /**
     * 获取过期时间（单位：秒）
     */
    public long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }
    
    /**
     * 判断key是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis判断key是否存在失败: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 自增（原子操作）
     */
    public long increment(String key, long delta) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Long value = ops.increment(key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Redis自增失败: key={}, delta={}", key, delta, e);
            return 0;
        }
    }
    
    /**
     * 自增1
     */
    public long increment(String key) {
        return increment(key, 1);
    }
    
    /**
     * 自减（原子操作）
     */
    public long decrement(String key, long delta) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Long value = ops.decrement(key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Redis自减失败: key={}, delta={}", key, delta, e);
            return 0;
        }
    }
    
    /**
     * 自减1
     */
    public long decrement(String key) {
        return decrement(key, 1);
    }
    
    /**
     * Hash操作 - 设置字段值
     */
    public void hset(String key, String field, Object value) {
        try {
            HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
            ops.put(key, field, value);
        } catch (Exception e) {
            log.error("Redis Hash设置失败: key={}, field={}", key, field, e);
        }
    }
    
    /**
     * Hash操作 - 获取字段值
     */
    public Object hget(String key, String field) {
        try {
            HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
            return ops.get(key, field);
        } catch (Exception e) {
            log.error("Redis Hash获取失败: key={}, field={}", key, field, e);
            return null;
        }
    }
    
    /**
     * Hash操作 - 获取所有字段值
     */
    public Map<String, Object> hgetAll(String key) {
        try {
            HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
            return ops.entries(key);
        } catch (Exception e) {
            log.error("Redis Hash获取所有字段失败: key={}", key, e);
            return new HashMap<>();
        }
    }
    
    /**
     * Hash操作 - 删除字段
     */
    public long hdelete(String key, String... fields) {
        try {
            HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
            return ops.delete(key, (Object[]) fields);
        } catch (Exception e) {
            log.error("Redis Hash删除字段失败: key={}, fields={}", key, Arrays.toString(fields), e);
            return 0;
        }
    }
    
    /**
     * Hash操作 - 判断字段是否存在
     */
    public boolean hexists(String key, String field) {
        try {
            HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
            return ops.hasKey(key, field);
        } catch (Exception e) {
            log.error("Redis Hash判断字段是否存在失败: key={}, field={}", key, field, e);
            return false;
        }
    }
    
    /**
     * List操作 - 左推入
     */
    public long lpush(String key, Object value) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            Long size = ops.leftPush(key, value);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis List左推入失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * List操作 - 右推入
     */
    public long rpush(String key, Object value) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            Long size = ops.rightPush(key, value);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis List右推入失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * List操作 - 左弹出
     */
    public Object lpop(String key) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            return ops.leftPop(key);
        } catch (Exception e) {
            log.error("Redis List左弹出失败: key={}", key, e);
            return null;
        }
    }
    
    /**
     * List操作 - 右弹出
     */
    public Object rpop(String key) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            return ops.rightPop(key);
        } catch (Exception e) {
            log.error("Redis List右弹出失败: key={}", key, e);
            return null;
        }
    }
    
    /**
     * List操作 - 获取列表范围
     */
    public List<Object> lrange(String key, long start, long end) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            return ops.range(key, start, end);
        } catch (Exception e) {
            log.error("Redis List获取范围失败: key={}, start={}, end={}", key, start, end, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * List操作 - 获取列表长度
     */
    public long llen(String key) {
        try {
            ListOperations<String, Object> ops = redisTemplate.opsForList();
            Long size = ops.size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis List获取长度失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * Set操作 - 添加元素
     */
    public long sadd(String key, Object... values) {
        try {
            SetOperations<String, Object> ops = redisTemplate.opsForSet();
            Long size = ops.add(key, values);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis Set添加元素失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * Set操作 - 移除元素
     */
    public long srem(String key, Object... values) {
        try {
            SetOperations<String, Object> ops = redisTemplate.opsForSet();
            Long size = ops.remove(key, values);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis Set移除元素失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * Set操作 - 获取所有元素
     */
    public Set<Object> smembers(String key) {
        try {
            SetOperations<String, Object> ops = redisTemplate.opsForSet();
            return ops.members(key);
        } catch (Exception e) {
            log.error("Redis Set获取所有元素失败: key={}", key, e);
            return new HashSet<>();
        }
    }
    
    /**
     * Set操作 - 判断元素是否存在
     */
    public boolean sismember(String key, Object value) {
        try {
            SetOperations<String, Object> ops = redisTemplate.opsForSet();
            return Boolean.TRUE.equals(ops.isMember(key, value));
        } catch (Exception e) {
            log.error("Redis Set判断元素是否存在失败: key={}, value={}", key, value, e);
            return false;
        }
    }
    
    /**
     * Set操作 - 获取集合大小
     */
    public long scard(String key) {
        try {
            SetOperations<String, Object> ops = redisTemplate.opsForSet();
            Long size = ops.size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis Set获取大小失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * ZSet操作 - 添加元素（带分数）
     */
    public boolean zadd(String key, Object value, double score) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return Boolean.TRUE.equals(ops.add(key, value, score));
        } catch (Exception e) {
            log.error("Redis ZSet添加元素失败: key={}, score={}", key, score, e);
            return false;
        }
    }
    
    /**
     * ZSet操作 - 获取分数
     */
    public Double zscore(String key, Object value) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.score(key, value);
        } catch (Exception e) {
            log.error("Redis ZSet获取分数失败: key={}, value={}", key, value, e);
            return null;
        }
    }
    
    /**
     * ZSet操作 - 获取排名（升序）
     */
    public Long zrank(String key, Object value) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.rank(key, value);
        } catch (Exception e) {
            log.error("Redis ZSet获取排名失败: key={}, value={}", key, value, e);
            return null;
        }
    }
    
    /**
     * ZSet操作 - 获取排名（降序）
     */
    public Long zrevrank(String key, Object value) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.reverseRank(key, value);
        } catch (Exception e) {
            log.error("Redis ZSet获取反向排名失败: key={}, value={}", key, value, e);
            return null;
        }
    }
    
    /**
     * ZSet操作 - 获取范围（按分数升序）
     */
    public Set<Object> zrange(String key, long start, long end) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.range(key, start, end);
        } catch (Exception e) {
            log.error("Redis ZSet获取范围失败: key={}, start={}, end={}", key, start, end, e);
            return new HashSet<>();
        }
    }
    
    /**
     * ZSet操作 - 获取范围（按分数降序）
     */
    public Set<Object> zrevrange(String key, long start, long end) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("Redis ZSet获取反向范围失败: key={}, start={}, end={}", key, start, end, e);
            return new HashSet<>();
        }
    }
    
    /**
     * ZSet操作 - 获取分数范围内的元素
     */
    public Set<Object> zrangeByScore(String key, double min, double max) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            return ops.rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Redis ZSet获取分数范围失败: key={}, min={}, max={}", key, min, max, e);
            return new HashSet<>();
        }
    }
    
    /**
     * ZSet操作 - 获取集合大小
     */
    public long zcard(String key) {
        try {
            ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
            Long size = ops.size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis ZSet获取大小失败: key={}", key, e);
            return 0;
        }
    }
    
    /**
     * 发布消息
     */
    public void publish(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("Redis发布消息失败: channel={}", channel, e);
        }
    }
    
    /**
     * 获取锁
     */
    public boolean acquireLock(String lockKey, String requestId, long expireTime) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            return Boolean.TRUE.equals(ops.setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("Redis获取锁失败: lockKey={}, requestId={}", lockKey, requestId, e);
            return false;
        }
    }
    
    /**
     * 释放锁
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            String currentValue = getString(lockKey);
            if (requestId.equals(currentValue)) {
                return delete(lockKey);
            }
            return false;
        } catch (Exception e) {
            log.error("Redis释放锁失败: lockKey={}, requestId={}", lockKey, requestId, e);
            return false;
        }
    }
    
    /**
     * 批量操作 - 管道模式
     */
    public List<Object> executePipeline(List<RedisOperation> operations) {
        try {
            return redisTemplate.executePipelined((connection) -> {
                for (RedisOperation operation : operations) {
                    operation.execute(connection);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Redis管道操作失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 清空当前数据库
     */
    public void flushDb() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
        } catch (Exception e) {
            log.error("Redis清空数据库失败", e);
        }
    }
    
    /**
     * 获取Redis信息
     */
    public Properties info() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().info();
        } catch (Exception e) {
            log.error("Redis获取信息失败", e);
            return new Properties();
        }
    }
    
    /**
     * 获取数据库大小
     */
    public Long dbSize() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().dbSize();
        } catch (Exception e) {
            log.error("Redis获取数据库大小失败", e);
            return 0L;
        }
    }
    
    /**
     * Redis操作接口
     */
    public interface RedisOperation {
        void execute(org.springframework.data.redis.connection.RedisConnection connection);
    }
}
