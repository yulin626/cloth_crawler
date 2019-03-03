package com.cloth.crawler.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author: YuLin
 * @description:
 * @date: created in 13:53 2019/2/24
 * @modified by:
 */
@Configuration
@ComponentScan(basePackages = "com.cloth.crawler.*")
@PropertySource(value = {"classpath:application.properties"},ignoreResourceNotFound = true)
public class AppConfig {
}
