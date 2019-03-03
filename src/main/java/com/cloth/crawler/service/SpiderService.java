package com.cloth.crawler.service;

import com.alibaba.fastjson.JSON;
import com.cloth.crawler.common.CommonSpider;
import com.cloth.crawler.model.ResultBundle;
import com.cloth.crawler.model.ResultBundleBuilder;
import com.cloth.crawler.model.ResultListBundle;
import com.cloth.crawler.model.SpiderInfo;
import com.cloth.crawler.util.HttpUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.JMException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: YuLin
 * @description:
 * @date: created in 13:08 2019/2/24
 * @modified by:
 */
@Service
@Slf4j
public class SpiderService {
    @Autowired
    private CommonSpider commonSpider;
    @Autowired
    private ResultBundleBuilder bundleBuilder;

    /**
     * 启动爬虫
     *
     * @param spiderInfo 爬虫配置信息spiderinfo
     * @return 任务id
     */
    public ResultBundle<String> start(SpiderInfo spiderInfo) {
        return bundleBuilder.bundle(spiderInfo.toString(), () -> commonSpider.start(spiderInfo));
    }

    /**
     * 启动爬虫
     *
     * @param spiderInfoJson 使用json格式进行序列化的spiderinfo
     * @return 任务id
     */
    public ResultBundle<String> start(String spiderInfoJson) {
        Preconditions.checkArgument(StringUtils.isNotBlank(spiderInfoJson), "爬虫配置为空");
        SpiderInfo spiderInfo = JSON.parseObject(spiderInfoJson, SpiderInfo.class);
        return start(spiderInfo);
    }

    /**
     * 停止爬虫
     *
     * @param uuid 任务id(爬虫uuid)
     * @return
     */
    public ResultBundle<String> stop(String uuid) {
        return bundleBuilder.bundle(uuid, () -> {
            commonSpider.stop(uuid);
            return "OK";
        });
    }

    /**
     * 删除爬虫
     *
     * @param uuid 爬虫uuid 任务id
     * @return
     */
    public ResultBundle<String> delete(String uuid) {
        return bundleBuilder.bundle(uuid, () -> {
            commonSpider.delete(uuid);
            return "OK";
        });
    }

    /**
     * 删除所有爬虫
     *
     * @return
     */
    public ResultBundle<String> deleteAll() {
        return bundleBuilder.bundle(null, () -> {
            commonSpider.deleteAll();
            return "OK";
        });
    }

    /**
     * 根据爬虫配置ID批量启动任务
     *
     * @param spiderInfoList 爬虫配置ID列表
     * @return 配置id列表
     */
    public ResultListBundle<String> startAll(List<SpiderInfo> spiderInfoList) {
        return bundleBuilder.listBundle(spiderInfoList.toString(), () -> {
            List<String> spiderInfoIdList = Lists.newArrayList();
            for (SpiderInfo info : spiderInfoList) {
                try {
                    String spiderInfoId = commonSpider.start(info);
                    spiderInfoIdList.add(spiderInfoId);
                } catch (JMException e) {
                    log.error("启动配置ID{}出错，{}", info.getId(), e);
                }
            }
            return spiderInfoIdList;
        });
    }
}
