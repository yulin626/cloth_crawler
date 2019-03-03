package com.cloth.crawler.model;

import lombok.Data;

/**
 * @author: YuLin
 * @description:
 * @date: created in 19:39 2019/2/26
 * @modified by:
 */
@Data
public class SpiderConfig {
    private String spiderConfigId;

    private String domain;

    private String startUrl;

    private String titleXpath;

    private String priceXpath;
}
