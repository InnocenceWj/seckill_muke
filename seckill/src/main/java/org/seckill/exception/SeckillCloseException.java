package org.seckill.exception;

/**
 * @创建人 wj
 * @创建时间 2018/10/26
 * @描述 秒杀关闭异常
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
