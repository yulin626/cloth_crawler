package com.cloth.crawler.pipeline;

import com.alibaba.fastjson.JSON;
import com.cloth.crawler.model.QueryModel;
import com.cloth.crawler.model.StaticValue;
import com.cloth.crawler.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;

/**
 * @author: YuLin
 * @description:
 * @date: created in 21:56 2019/2/20
 * @modified by:
 */
@Slf4j
@Component
public class MongoDBPipeline implements Pipeline {

    @Autowired
    private StaticValue staticValue;

    @Override
    public void process(ResultItems resultItems, Task task) {
        QueryModel queryModel = new QueryModel();
        queryModel.setQueryName("getKeyWord");
        queryModel.setWithoutType("true");
        try {
            HttpUtil.sendPost(staticValue.getQueryUrl(), JSON.toJSONString(queryModel));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
