package com.cloth.crawler.common;

import com.cloth.crawler.model.SpiderInfo;
import com.cloth.crawler.model.StaticValue;
import com.cloth.crawler.pipeline.MongoDBPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.management.JMException;
import java.net.BindException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: YuLin
 * @description:
 * @date: created in 21:37 2019/2/20
 * @modified by:
 */
@Slf4j
@Component
public class CommonSpider {

    private StaticValue staticValue;
    private MongoDBPipeline mongoDBPipeline;
    //    private static List<String> ignoredUrls;
    private Map<String, MySpider> spiderMap = new HashMap<>();

    @Autowired
    public CommonSpider(StaticValue staticValue, MongoDBPipeline mongoDBPipeline) throws InterruptedException, BindException {
        this.staticValue = staticValue;
        this.mongoDBPipeline = mongoDBPipeline;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deleteAll();
                log.debug("定时删除全部完成的普通网页抓取任务");
            }
        }, staticValue.getTaskDeleteDelay() * 3600000, staticValue.getTaskDeletePeriod() * 3600000);
        log.debug("定时删除普通网页抓取任务记录线程已启动,延时:{}小时,每{}小时删除一次", staticValue.getTaskDeleteDelay(), staticValue.getTaskDeletePeriod());
    }

    private final PageConsumer spiderInfoPageConsumer = (page, info) -> {
        try {
            long start = System.currentTimeMillis();
            //本页是否是首页
            final boolean startPage = info.getStartURL().contains(page.getUrl().get());

            if (StringUtils.isNotBlank(info.getTitleXpath())){
                //title
                var titleList = page.getHtml().xpath(info.getTitleXpath()).all();
                page.putField("titleList", titleList);
            }


            page.putField("domain", info.getDomain());
            page.putField("url", page.getUrl().get());
            page.putField("keyWordId", info.getKeyWordId());
            page.putField("length", page.getRawText().length());
            if (info.isSaveCapture()) {
                page.putField("rawHTML", page.getHtml().get());
            }
        } catch (Exception e) {
            log.error("处理网页出错，%s", e.toString());
        }
    };

    /**
     * 启动爬虫
     *
     * @param info 爬虫配置信息
     * @return
     * @throws JMException
     */
    public String start(SpiderInfo info) throws JMException {
        QueueScheduler scheduler = new QueueScheduler() {
            @Override
            public void pushWhenNoDuplicate(Request request, Task task) {
                int left = getLeftRequestsCount(task);
                if (left <= staticValue.getLimitOfCommonWebpageDownloadQueue()) {
                    super.pushWhenNoDuplicate(request, task);
                }
            }
        };

//        //ip代理
//        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//        if (info.getIps().size() > 0) {
//            List<Map<String, String>> ips = new ArrayList<>();
//            httpClientDownloader.setProxyProvider(
//                    SimpleProxyProvider.from(new Proxy(ips.get(0).get("ip"), Integer.parseInt(ips.get(0).get("port"))),
//                            new Proxy(ips.get(1).get("ip"), Integer.parseInt(ips.get(1).get("port"))),
//                            new Proxy(ips.get(2).get("ip"), Integer.parseInt(ips.get(2).get("port"))),
//                            new Proxy(ips.get(3).get("ip"), Integer.parseInt(ips.get(3).get("port"))),
//                            new Proxy(ips.get(4).get("ip"), Integer.parseInt(ips.get(4).get("port")))));
//        }

        MySpider spider = (MySpider) makeSpider(info)
                .setScheduler(scheduler)
                .setDownloader(new SeleniumDownloader(""))
                .addPipeline(mongoDBPipeline);
//        info.getStartURL().forEach(s -> scheduler.pushWhenNoDuplicate(new Request(s), spider));
        spiderMap.put(info.getId(), spider);
        spider.start();
        return info.getId();
    }

    /**
     * 停止爬虫
     *
     * @param uuid
     */
    public void stop(String uuid) {
        Preconditions.checkArgument(spiderMap.containsKey(uuid), "找不到UUID为%s的爬虫,请检查参数", uuid);
        spiderMap.get(uuid).close();
        spiderMap.get(uuid).stop();
    }

    /**
     * 删除爬虫
     *
     * @param uuid
     */
    public void delete(String uuid) {
//        Preconditions.checkArgument(spiderMap.containsKey(uuid) || taskManager.getTaskById(uuid) != null, "找不到UUID为%s的爬虫,请检查参数", uuid);
//        Preconditions.checkArgument(taskManager.getTaskById(uuid).getState() == State.STOP, "爬虫" + uuid + "尚未停止,不能删除任务");
        spiderMap.remove(uuid);
    }

    /**
     * 删除全部爬虫
     */
    public void deleteAll() {
        List<String> spiderUUID2BeRemoved = spiderMap.entrySet().stream().filter(
                spiderEntry -> spiderEntry.getValue().getStatus() == Spider.Status.Stopped
        ).map(Map.Entry::getKey).collect(Collectors.toList());
        for (String uuid : spiderUUID2BeRemoved) {
            try {
                spiderMap.remove(uuid);
            } catch (Exception e) {
                log.error("删除任务ID:{}出错,{}", uuid, e.getLocalizedMessage());
            }
        }
    }

    /**
     * 生成爬虫
     *
     * @param info 抓取模板
     * @return
     */
    private MySpider makeSpider(SpiderInfo info) {
        MySpider spider = (MySpider) new MySpider(new MyPageProcessor(info), info)
                .thread(info.getThread())
                .setUUID(info.getId())
                .addUrl(info.getStartURL());

//        spider.setDownloader();
        return spider;
    }

    /**
     * 在原有的webmagic基础上添加了一些其他功能
     */
    private class MySpider extends Spider {
        private final SpiderInfo SPIDER_INFO;

        MySpider(PageProcessor pageProcessor, SpiderInfo spiderInfo) {
            super(pageProcessor);
            this.SPIDER_INFO = spiderInfo;
        }
    }

    private class MyPageProcessor implements PageProcessor {
        private Site site;
        private SpiderInfo info;

        public MyPageProcessor(SpiderInfo info) {
            this.site = Site.me().setDomain(info.getDomain()).setTimeOut(info.getTimeout())
                    .setRetryTimes(info.getRetry()).setSleepTime(info.getSleep())
                    .setCharset(StringUtils.isBlank(info.getCharset()) ? null : info.getCharset())
                    .setUserAgent(info.getUserAgent())
                    .addHeader("cookie","cna=F6XHE83q1W0CAXLhVjvdxaN1; _med=dw:1280&dh:720&pw:1920&ph:1080&ist:0; UM_distinctid=166c2b316e62a7-070ceeb9b17dd6-b79183d-e1000-166c2b316e715e; lid=laputayl; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; x=__ll%3D-1%26_ato%3D0; enc=jYf4dAUO8pLWk%2FzUSUHqCOFURP8KFpOzgt7FUyxG4FYIK8l4uBDb%2FB4LZNhWlIiUWD93bhxj3yXbvvMs%2B0ZLog%3D%3D; hng=CN%7Czh-CN%7CCNY%7C156; dnk=laputayl; tracknick=laputayl; lgc=laputayl; cookie2=129a844eccc2fe76ce0f56a238129c0e; t=a613d315466ab689da042b4c52ad990f; _tb_token_=f31bb53e56eee; _uab_collina=154831015145919997809369; swfstore=84395; l=bBjNGD2evP9GNx8-BOCgquI-nJbtvIRjsuPRwJJ6i_5dp1Y1lC_Oluztyev6Vj5POo8B4z6vzNptPeoaJyTN.; ck1=\"\"; _m_h5_tk=a90e27069364516476e7b25e71b29ab0_1551256941397; _m_h5_tk_enc=eb487f4ec5e4bd6997020907457529e6; uc1=cookie16=WqG3DMC9UpAPBHGz5QBErFxlCA%3D%3D&cookie21=W5iHLLyFeYZ1WM9hVnmS&cookie15=UtASsssmOIJ0bQ%3D%3D&existShop=false&pas=0&cookie14=UoTZ5bHjp77LWg%3D%3D&cart_m=0&tag=8&lng=zh_CN; uc3=vt3=F8dByEzWmlgMotZT2IU%3D&id2=W8HbYxhKY1mY&nk2=D8L8skGspK0%3D&lg2=URm48syIIVrSKA%3D%3D; _l_g_=Ug%3D%3D; unb=881829310; cookie1=BxE16W3YI%2Bcnz2VEKUPSGqOD0CBfZMWlSIpoHx9srjs%3D; login=true; cookie17=W8HbYxhKY1mY; _nk_=laputayl; uss=\"\"; csg=bea087c3; skt=103b79be8cf8eadc; tt=tmall-main; res=scroll%3A1249*6064-client%3A1249*194-offset%3A1249*6064-screen%3A1280*720; pnm_cku822=098%23E1hvp9vUvbpvUpCkvvvvvjiPRLzUzj1En2SwAjljPmPh6jYURsSOsjrnPLqO1jDRPTwCvvBvpvpZRphvChCvvvvEvpCWBHcov8R1VAgaW4c6%2Bul08zYRQWvwVciPwyNO%2B2Kz8Zl9ZRAn%2BbyDCcHbAXZTKFEwOvxrg8TJEctApc7Q%2BulAbz6Jfw9XdeQEVA3lYUyCvvXmp99he1eivpvUphvhiXJ%2BPL8tvpvIphvvvvvvphCvpC2bvvCv9yCvHvvvvh8ophvZvvvvpKivpCpvvvCmT9hCvvXvovvvvvvPvpvhvvvvvv%3D%3D; cq=ccp%3D0; whl=-1%260%260%260; x5sec=7b22746d616c6c7365617263683b32223a226662306433643337633333323162306537653638613638363261316133333365434d322b32754d46454f2b4970654b3775367a4835514561437a67344d5467794f544d784d447378227d; isg=BHh4loUzUK1xZbsDRFv8fjpdSSbKSdwMJKhxfbLpr7NqzRm3WvNp-83shIVYwJRD");
            this.info = info;
        }

        @Override
        public void process(Page page) {
            spiderInfoPageConsumer.accept(page, info);
        }

        @Override
        public Site getSite() {
            return site;
        }
    }
}
