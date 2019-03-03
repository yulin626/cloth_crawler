package com.cloth.crawler.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: YuLin
 * @description:
 * @date: created in 19:58 2019/2/26
 * @modified by:
 */
@Data
public class QueryData<T> implements Serializable {

    private List<T> data;

    private int totalCount;
}
