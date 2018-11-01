package org.seckill.redis;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @创建人 wj
 * @创建时间 2018/10/30
 * @描述
 */
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;


    private RuntimeSchema<Seckill> runtimeSchema = RuntimeSchema.createFrom(Seckill.class);


    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(long seckillId) {
        //        Redis 操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //    redis 并没有实现内部序列化
                //    get -> byte[] -> 反序列化 -> Object(Seckill)
                //    采用自定义序列化
                //    protostuff ：pojo
                //    这样写压缩空间是原来的十分之一，速度是原来的两倍
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //    seckill 空对象
                    Seckill seckill = runtimeSchema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes, seckill, runtimeSchema);
                    //    seckill 被反序列化
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        //        set Object（Seckill） -> 序列化 ->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                //  第三个参数是缓存器
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, runtimeSchema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //   超时缓存，1小时
                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }
}
