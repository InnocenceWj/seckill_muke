package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @创建人 wj
 * @创建时间 2018/10/25
 * @描述 需要配置spring和junit的整合，为了junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:config/spring-dao.xml"})
public class SuccessKilledMapperTest {
    @Resource
    private SuccessKilledMapper successKilledMapper;

    @Test
    public void insertSuccessKilled() {
        int num=successKilledMapper.insertSuccessKilled(1000L,17687191257L);
        System.out.println(num);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled=successKilledMapper.queryByIdWithSeckill(1000L,17687191257L);
        System.out.println(successKilled);
    }
}