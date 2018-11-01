package org.seckill.exception;

/**
 * @创建人 wj
 * @创建时间 2018/10/26
 * @描述
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
