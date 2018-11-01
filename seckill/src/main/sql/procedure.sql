--秒杀执行存储过程
DELIMITER $$ --console;转换为$$
--定义存储过程
--参数：in 输入参数 ；out 输出参数
--ROW_COUNT ():返回上一条修改类型sql（delete,insert,update）的影响行数
--ROW_COUNT () 0:未修改数据  >0表示修改的行数 <0：sql错误/未执行修改sql
--seckill_muke:数据库名称 ，别写错了！！
CREATE PROCEDURE `seckill_muke`.`execute_seckill` (
	IN v_seckill_id BIGINT,
	IN v_phone BIGINT,
	IN v_kill_time TIMESTAMP,
	OUT r_result INT
)
BEGIN
	DECLARE
		insert_count INT DEFAULT 0;

START TRANSACTION;

-- 每个语句后不能少; --
INSERT IGNORE INTO success_killed (
	seckill_id,
	user_phone,
	create_time
)
VALUES
	(
		v_seckill_id,
		v_phone,
		v_kill_time
	);

SELECT
	ROW_COUNT() INTO insert_count;


IF (insert_count = 0) THEN
	ROLLBACK;
  SET r_result =- 1;
ELSEIF (insert_count < 0) THEN
	ROLLBACK;
 SET r_result =- 2;
ELSE
	UPDATE seckill
  SET number = number - 1
  WHERE
	 seckill_id = v_seckill_id
   AND start_time < v_kill_time
   AND end_time > v_kill_time
   AND number > 0;

SELECT
	ROW_COUNT() INTO insert_count;

IF (insert_count = 0) THEN
	ROLLBACK;
 SET r_result =- 1;
ELSEIF (insert_count < 0) THEN
	ROLLBACK;
  SET r_result =- 2;
ELSE
	COMMIT;
  SET r_result = 1;
END IF;
END IF;
END; $$
--存储过程定义结束

DELIMITER ;
set @r_result=-3;
-- 执行存储过程
call execute_seckill(1003,13502178891,now(),@r_result);
-- 获取结果
select @r_result;

--1.存储过程优化:事务行级锁持有的时间
--2.不要过分依赖存储过程：互联网用的不多
--3.简单逻辑可以运用存储过程
--4.QPS一个秒杀单6000/gps