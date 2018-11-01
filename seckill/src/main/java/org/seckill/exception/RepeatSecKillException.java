package org.seckill.exception;

/**
 * @创建人 wj
 * @创建时间 2018/10/26
 * @描述 重复秒杀异常（运行期异常）
 */
public class RepeatSecKillException extends SeckillException {

    public RepeatSecKillException(String message) {
        super(message);
    }

    public RepeatSecKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
