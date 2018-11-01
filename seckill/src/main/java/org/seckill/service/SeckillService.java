package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * @创建人 wj
 * @创建时间 2018/10/25
 * @描述 业务接口：站在“使用者”角度设计接口 三个方面：方法定义粒度，参数，返回类型
 */
public interface SeckillService {
    /**
     * @return List<Seckill>
     * @params
     * @author wj
     * @description 查询所有秒杀记录
     */
    List<Seckill> getseckillList();

    /**
     * @return Seckill
     * @params seckillId
     * @author wj
     * @description 根据Id查询秒杀记录
     */
    Seckill getById(long seckillId);


    /**
     * @return
     * @params
     * @author wj
     * @description 秒杀开启时输入秒杀接口地址，否则输出系统时间和秒杀时间
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     *@params
     *@return
     *@author  wj
     *@description 执行秒杀操作
     */
    SeckillExecution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException,RepeatSecKillException,SeckillCloseException;

    /**
     *@params
     *@return
     *@author  wj
     *@description 使用 存储过程 执行秒杀操作
     */
    SeckillExecution excuteSeckillProcedure(long seckillId, long userPhone, String md5) throws SeckillException,RepeatSecKillException,SeckillCloseException;
}
