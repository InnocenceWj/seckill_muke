package org.seckill.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillMapper;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @创建人 wj
 * @创建时间 2018/10/30
 * @描述
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:config/spring-dao.xml"})
public class RedisDaoTest {
    @Resource
    private RedisDao redisDao;
    @Resource
    private SeckillMapper seckillMapper;

    @Test
    public void testSeckill() throws Exception {
        long id = 1002L;
        //        get and put
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            seckill = seckillMapper.queryById(id);
            if (seckill != null) {
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }
}