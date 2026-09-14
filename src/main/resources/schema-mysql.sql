-- MySQL 8 建表脚本，幂等（CREATE TABLE IF NOT EXISTS）。
-- 执行路径有二：
--   1) docker-compose up 首次启动 MySQL 容器时，由 /docker-entrypoint-initdb.d 自动执行
--   2) 应用启动时 Spring SQL Init 执行（application.yml 中 sql.init.mode: always）
-- 也可以手动用 mysql 客户端 source 本脚本。
CREATE TABLE IF NOT EXISTS fan_board (
    slot       INT PRIMARY KEY,
    speed      VARCHAR(16) NOT NULL,
    mode       VARCHAR(16) NOT NULL,
    updated_at DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS business_board (
    slot        INT PRIMARY KEY,
    temperature DOUBLE NOT NULL,
    updated_at  DATETIME(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS alarm_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot        INT NOT NULL,
    temperature DOUBLE NOT NULL,
    alarm_time  DATETIME(3) NOT NULL,
    INDEX idx_alarm_time (alarm_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
