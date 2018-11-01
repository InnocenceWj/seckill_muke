package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @创建人 wj
 * @创建时间 2018/10/25
 * @描述 需要配置spring和junit的整合，为了junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:config/spring-dao.xml"})
public class SeckillMapperTest {
    @Resource
    private SeckillMapper seckillMapper;

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillMapper.queryById(id);
        System.out.println(seckill);
    }

    //    java没有保存形参的记录，queryAll(offset, limit)会变成 queryAll(arg0, arg1);如果在mapper接口方法的参数前不加@Param(value = "offset") 会报错
    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillMapper.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() {
        Date killTime=new Date();
        int updateCount=seckillMapper.reduceNumber(1000L,killTime);
        System.out.println(updateCount);
    }


}