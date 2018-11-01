package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.seckill.entity.SuccessKilledKey;

public interface SuccessKilledMapper {
    //    插入购买明细，可过滤重复
    int insertSuccessKilled(@Param(value = "seckillId") long seckillId,@Param(value = "userPhone") long userPhone);

    //    根据Id查uxnSuccessKilled并携带秒杀产品对象实体
    SuccessKilled queryByIdWithSeckill(@Param(value = "seckillId") long seckillId,@Param(value = "userPhone") long userPhone);

    int deleteByPrimaryKey(SuccessKilledKey key);

    int insert(SuccessKilled record);

    int insertSelective(SuccessKilled record);

    SuccessKilled selectByPrimaryKey(SuccessKilledKey key);

    int updateByPrimaryKeySelective(SuccessKilled record);

    int updateByPrimaryKey(SuccessKilled record);
}