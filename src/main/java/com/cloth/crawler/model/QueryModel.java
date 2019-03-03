package com.cloth.crawler.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: YuLin
 * @description:
 * @date: created in 19:16 2019/2/26
 * @modified by:
 */
@Data
public class QueryModel implements Serializable {

    private String queryName;

    private String withoutType;
}
