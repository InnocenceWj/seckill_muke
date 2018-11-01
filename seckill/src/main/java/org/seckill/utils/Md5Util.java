package org.seckill.utils;

import org.springframework.util.DigestUtils;

/**
 * @创建人 wj
 * @创建时间 2018/10/26
 * @描述
 */
public class Md5Util {

    //    用于混淆的盐值
    private static String salt = "jhdgskdjbi938uhdskahflsdjsan";

    public static String getMd5Str(long seckillId){
        String base=seckillId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
