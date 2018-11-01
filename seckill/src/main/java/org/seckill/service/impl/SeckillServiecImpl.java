package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillMapper;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSecKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.redis.RedisDao;
import org.seckill.service.SeckillService;
import org.seckill.utils.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 wj
 * @创建时间 2018/10/25
 * @描述
 */
@Service
public class SeckillServiecImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillMapper seckillMapper;
    @Resource
    private RedisDao redisDao;
    @Resource
    private SuccessKilledMapper successKilledMapper;

    public List<Seckill> getseckillList() {
        return seckillMapper.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //   ****优化点：缓存优化 超时的基础上维护一致性
        //   1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //    2.访问数据库
            seckill = seckillMapper.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }
        long startTime = seckill.getStartTime().getTime();
        long endTime = seckill.getEndTime().getTime();
        long nowTime = new Date().getTime();
        if (nowTime < startTime | nowTime > endTime) {
            return new Exposer(false, nowTime, startTime, endTime, seckillId);
        }
        //   md5转换特定字符串的过程，不可逆
        return new Exposer(true, Md5Util.getMd5Str(seckillId), seckillId);
    }

    /**
     * 使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPCHTml请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读不需要事务控制
     */
    @Transactional
    public SeckillExecution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatSecKillException, SeckillCloseException {
//        if (md5 == null || !md5.equals(Md5Util.getMd5Str(seckillId))) {
//            throw new SeckillException("seckill data rewrite");
//        }
        try {
            int insertNum = successKilledMapper.insertSuccessKilled(seckillId, userPhone);
//            唯一：seckillId+userPhone
            if (insertNum <= 0) {
                throw new RepeatSecKillException("seckill is repeated");
            } else {
                // 执行秒杀逻辑：减库存+记录购买行为
                // 减库存，热点商品竞争
                int updateNum = seckillMapper.reduceNumber(seckillId, new Date());
                if (updateNum <= 0) {
                    //  没有更新操作，rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功,commit
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
//          先抛出秒杀关闭异常和重复秒杀异常，否则全部会变成SeckillException
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatSecKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
//            把编译异常转换为运行异常
            throw new SeckillException("Seckill System error");
        }
    }

    /**
     * @return
     * @params
     * @author wj
     * @description 使用 存储过程 执行秒杀操作
     */
    public SeckillExecution excuteSeckillProcedure(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !md5.equals(Md5Util.getMd5Str(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
//        执行存储过程，result被赋值
        try {
            seckillMapper.killByProcedure(map);
//            获取result,MapUtils:需要导入commons-collections依赖
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new SeckillExecution(seckillId, SeckillStateEnum.SYSTEM_ERROR);
        }
    }
}
