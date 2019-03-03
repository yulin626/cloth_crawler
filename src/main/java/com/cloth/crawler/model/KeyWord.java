package com.cloth.crawler.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: YuLin
 * @description:
 * @date: created in 19:38 2019/2/26
 * @modified by:
 */
@Data
public class KeyWord implements Serializable {
    private String id;

    private String keyWord;

    private String isDeleted;
}
