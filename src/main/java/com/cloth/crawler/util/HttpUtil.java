package com.cloth.crawler.util;

import com.alibaba.fastjson.JSON;
import com.cloth.crawler.model.MongoOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: YuLin
 * @description:
 * @date: created in 14:18 2019/2/24
 * @modified by:
 */
@Slf4j
public class HttpUtil {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    // 超时设置
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setSocketTimeout(10000)
            .build();

    // 编码设置
    private static final ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Consts.UTF_8)
            .build();

    private static HttpClientBuilder getBuilder() {
        List<Header> headers = new ArrayList<>();
        Header header = new BasicHeader("User-Agent", USER_AGENT);
        headers.add(header);
        return HttpClients.custom().setDefaultConnectionConfig(connectionConfig).setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig);
    }

    /**
     * 发送HttpGet请求
     *
     * @param url 请求地址
     * @return
     */
    public static String sendGet(String url) throws IOException {
        String result;
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpClient httpclient = getBuilder().build();
             CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
        }
        return result;
    }

    /**
     * 发送HttpGet请求
     *
     * @param url 请求地址
     * @return
     */
    public static CloseableHttpResponse sendGetResponse(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpclient = getBuilder().build();
        CloseableHttpResponse response = httpclient.execute(httpGet);

        return response;
    }

    /**
     * 发送HttpPost请求，参数为json字符串
     *
     * @param url            请求地址
     * @param jsonStr        json字符串
     * @param requestConfigs 请求config
     * @return
     */
    public static String sendPost(String url, String jsonStr, RequestConfig... requestConfigs) throws IOException {
        String result = "";

        // 设置entity
        StringEntity stringEntity = new StringEntity(jsonStr, Consts.UTF_8);
        stringEntity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(url);
        if (requestConfigs.length > 0) {
            httpPost.setConfig(requestConfigs[0]);
        }

        httpPost.setEntity(stringEntity);

        try (CloseableHttpClient httpclient = getBuilder().build(); CloseableHttpResponse httpResponse = httpclient.execute(httpPost);) {
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String url = "http://172.16.1.109:20006/dynamicCreateEntity";
        MongoOutput mongoOutput = new MongoOutput();
        mongoOutput.setTableId("c89e7e0a-c030-4893-1f28-a3ed187f2e7a");
        mongoOutput.setUserId("eb72ca66-8977-9d59-d569-1571d8e6c289");

        Map<String, String> entityData = new HashMap<>();
        entityData.put("a4a196b1-0bb3-7df1-8c99-9af8c899eb61", "test");  //关键字
        entityData.put("4b5557b7-4cf4-d5dc-99e6-e2cfbf8fd953", "test");    //内容
        entityData.put("1487e904-c482-480f-9bb3-57bc678c2405", "test");  //url

        mongoOutput.setEntityData(entityData);
        var result = HttpUtil.sendPost(url, JSON.toJSONString(mongoOutput));
        System.out.printf(result);
    }
}
