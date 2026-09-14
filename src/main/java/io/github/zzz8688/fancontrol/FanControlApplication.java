package io.github.zzz8688.fancontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 不使用 @MapperScan：Mapper 接口统一标注 @Mapper，由 mybatis-spring-boot-starter
 * 自动扫描，避免 @WebMvcTest 等切片测试在无数据源上下文里强制创建 Mapper 代理。
 */
@EnableScheduling
@SpringBootApplication
public class FanControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FanControlApplication.class, args);
    }
}
