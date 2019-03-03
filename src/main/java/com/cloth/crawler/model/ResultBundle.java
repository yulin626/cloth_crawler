package com.cloth.crawler.model;

/**
 * @author: YuLin
 * @description:
 * @date: created in 13:27 2019/2/24
 * @modified by:
 */
public class ResultBundle<T> {
    /**
     * 请求的参数
     */
    protected String keyword;
    /**
     * 返回结果的数量
     */
    protected int count;
    /**
     * 本次调用耗时
     */
    protected long time;
    /**
     * 本次调用是否成功
     */
    protected boolean success;
    /**
     * 如调用出现错误,错误信息
     */
    protected String errorMsg;
    /**
     * 本次调用的追踪ID
     */
    protected String traceId;
    /**
     * 结果
     */
    private T result;

    public ResultBundle() {
    }

    public ResultBundle(T result, String keyword, long time) {
        this.result = result;
        this.keyword = keyword;
        this.time = time;
        this.count = 1;
        this.success = true;
    }

    public ResultBundle(String keyword, long time, boolean success, String errorMsg) {
        result = null;
        this.success = success;
        this.errorMsg = errorMsg;
        this.keyword = keyword;
        this.time = time;
        this.count = 0;
    }
}
