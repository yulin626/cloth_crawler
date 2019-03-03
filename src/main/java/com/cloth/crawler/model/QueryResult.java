package com.cloth.crawler.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: YuLin
 * @description:
 * @date: created in 19:34 2019/2/26
 * @modified by:
 */
@Data
public class QueryResult<T> implements Serializable {

    private QueryData<T> data;

    private String message;

    private int status;
}

