package com.cloth.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloth.crawler.config.AppConfig;
import com.cloth.crawler.model.*;
import com.cloth.crawler.service.SpiderService;
import com.cloth.crawler.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        var commonSpiderService = context.getBean(SpiderService.class);
        CrawlerApplication test = new CrawlerApplication();
        commonSpiderService.startAll(test.spiderInfoList());
    }

    private List<SpiderInfo> spiderInfoList() {
        List<SpiderInfo> spiderInfoList = new ArrayList<>();
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        var staticValue = context.getBean(StaticValue.class);
        try {
            //获取keyword列表
            QueryModel queryModel = new QueryModel();
            queryModel.setQueryName("getKeyWord");
            queryModel.setWithoutType("true");
            var jsonWordResult = HttpUtil.sendPost(String.format(staticValue.getQueryUrl(), staticValue.getUserId(), staticValue.getToken()),
                    JSON.toJSONString(queryModel));
            var objWordResult = JSON.parseObject(jsonWordResult, new TypeReference<QueryResult<KeyWord>>(KeyWord.class) {
            });  //泛型解析，防止擦除
            var keywordList = objWordResult.getData().getData();

            //获取spiderConfig列表
            queryModel = new QueryModel();
            queryModel.setQueryName("getSpiderConfig");
            queryModel.setWithoutType("true");
            var jsonConfigResult = HttpUtil.sendPost(String.format(staticValue.getQueryUrl(), staticValue.getUserId(), staticValue.getToken()),
                    JSON.toJSONString(queryModel));
            var objConfigResult = JSON.parseObject(jsonConfigResult, new TypeReference<QueryResult<SpiderConfig>>(SpiderConfig.class) {
            });  //泛型解析，防止擦除
            var spiderConfigList = objConfigResult.getData().getData();

            SpiderInfo spiderInfo;
            for (SpiderConfig spiderConfig : spiderConfigList) {
                for (KeyWord keyWord : keywordList) {
                    spiderInfo = new SpiderInfo();
                    spiderInfo.setId(spiderConfig.getSpiderConfigId());
                    spiderInfo.setDomain(spiderConfig.getDomain());
                    spiderInfo.setKeyWordId(keyWord.getId());
                    if ("www.jd.com".equals(spiderConfig.getDomain())) {
                        spiderInfo.setStartURL(String.format(spiderConfig.getStartUrl(), keyWord.getKeyWord(), keyWord.getKeyWord()));
                    } else {
                        spiderInfo.setStartURL(spiderConfig.getStartUrl() + keyWord.getKeyWord());
                    }
                    spiderInfo.setTitleXpath(spiderConfig.getTitleXpath());
                    spiderInfo.setPriceXpath(spiderConfig.getPriceXpath());

                    spiderInfoList.add(spiderInfo);
                }
            }

        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return spiderInfoList;
    }
}
