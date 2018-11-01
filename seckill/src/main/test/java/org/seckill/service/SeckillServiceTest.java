package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillMapper;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @创建人 wj
 * @创建时间 2018/10/26
 * @描述
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:config/spring-service.xml", "classpath:config/spring-dao.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillService seckillService;

    @Test
    public void getseckillList() {
        List<Seckill> list=seckillService.getseckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() {
        Seckill seckill=seckillService.getById(1000);
        logger.info("seckill={}",seckill);
    }

    /**
     *@params
     *@return
     *@author  wj
     *@description 集成代码测试
     */
    @Test
    public void testSeckillLogic() {
        Exposer exposer=seckillService.exportSeckillUrl(1000);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            SeckillExecution seckillExecution=seckillService.excuteSeckill(1000,17687191257L,"a34b8301028863c990293a4ac89c77f3");
            logger.info("seckillExecution={}",seckillExecution);
        }else {
            logger.warn("exposer={}", exposer);
        }
    }

    /**
     *@params
     *@return
     *@author  wj
     *@description  测试存储过程
     */
    @Test
    public void excuteSeckillProcedure(){
        long seckillId=1003;
        long phone=17687191257L;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5=exposer.getMd5();
            SeckillExecution seckillExecution=seckillService.excuteSeckillProcedure(seckillId,phone,md5);
            logger.info(seckillExecution.getStateInfo());
        }
    }

}