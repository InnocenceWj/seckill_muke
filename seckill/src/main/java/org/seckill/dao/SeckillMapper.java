package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillMapper {
    //    减库存
    int reduceNumber(@Param(value = "seckillId") long seckillId, @Param(value = "killTime") Date seckillTime);

    //    根据Id查询秒杀对象
    Seckill queryById(long seckillId);

    //    根据偏移量查询秒杀商品列表
    //    java没有保存形参的记录，queryAll(offset, limit)会变成 queryAll(arg0, arg1);如果不加@Param(value = "offset") 会报错
    List<Seckill> queryAll(@Param(value = "offset") int offset, @Param(value = "limit") int limit);

    /**
     * @return
     * @params
     * @author wj
     * @description 使用 存储过程 执行秒杀
     */
    void killByProcedure(Map<String, Object> paramsMap);

    int deleteByPrimaryKey(Long seckillId);

    int insert(Seckill record);

    int insertSelective(Seckill record);

    Seckill selectByPrimaryKey(Long seckillId);

    int updateByPrimaryKeySelective(Seckill record);

    int updateByPrimaryKey(Seckill record);
}