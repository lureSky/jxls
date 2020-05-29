package com.martin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/5/29 0029
 */
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@MapperScan("com.martin.dao")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
