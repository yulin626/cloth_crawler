package com.cloth.crawler.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: YuLin
 * @description:
 * @date: created in 21:39 2019/2/20
 * @modified by:
 */
@Data
public class SpiderInfo {
    /**
     * 抓取配置id
     */
    private String id;
    /**
     * 使用多少抓取线程
     */
    private int thread = 1;
    /**
     * 失败的网页重试次数
     */
    private int retry = 2;
    /**
     * 抓取每个网页睡眠时间
     */
    private int sleep = 1000;
    /**
     * HTTP链接超时时间
     */
    private int timeout = 5000;
    /**
     * 是否只抓取首页
     */
    private boolean gatherFirstPage = false;
    /**
     * 网站名称
     */
    private String siteName;
    /**
     * 域名
     */
    private String domain;
    /**
     * 起始链接
     */
    private List<String> startURLs;
    /**
     * 起始链接
     */
    private String startURL;
    /**
     * 编码
     */
    private String charset;
    /**
     * 回调url
     */
    private List<String> callbackURL;
    /**
     * User Agent当前
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";
    //"Mozilla/5.0 (Windows NT 5.2) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
    /**
     * 是否保存网页快照,默认保存
     */
    private boolean saveCapture = true;

    /**
     * 代理ips
     */
    private List<Map<String,String>> ips = new ArrayList<>();
    /**
     * 关键字Id
     */
    private String keyWordId;

    /**
     * 标题xpath
     */
    private String titleXpath;
    /**
     * 价格xpath
     */
    private String priceXpath;
}
