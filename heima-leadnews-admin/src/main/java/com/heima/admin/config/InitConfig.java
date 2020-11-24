package com.heima.admin.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.heima.common.aliyun","com.heima.common.fastdfs","com.heima.seata.config"})
public class InitConfig {
}
