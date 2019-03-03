package com.cloth.crawler.common;

import com.cloth.crawler.model.SpiderInfo;
import us.codecraft.webmagic.Page;

/**
 * @author: YuLin
 * @description:
 * @date: created in 23:34 2019/2/22
 * @modified by:
 */
@FunctionalInterface
public interface PageConsumer {
    void accept(Page page, SpiderInfo info);
}
