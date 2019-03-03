package com.cloth.crawler.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: YuLin
 * @description:
 * @date: created in 21:40 2019/2/20
 * @modified by:
 */
@Data
@Component
public class StaticValue {
    /**
     * 删除任务延时,单位为小时
     */
    @Value("${spider.taskDeleteDelay}")
    private int taskDeleteDelay;
    /**
     * 删除任务时间间隔,单位为小时
     */
    @Value("${spider.taskDeletePeriod}")
    private int taskDeletePeriod;
    /**
     * 普通网页下载器队列最大长度限制
     */
    @Value("${spider.limitOfCommonWebpageDownloadQueue}")
    private int limitOfCommonWebpageDownloadQueue;

    @Value("${queryUrl}")
    private String queryUrl;
    @Value("${userId}")
    private String userId;
    @Value("${token}")
    private String token;

    private String cookie;
}
