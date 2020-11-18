package com.heima.seata;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SeataApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataApplication.class,args);
    }
}
