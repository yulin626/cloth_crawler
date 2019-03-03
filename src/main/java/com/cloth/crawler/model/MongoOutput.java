package com.cloth.crawler.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: YuLin
 * @description:
 * @date: created in 14:23 2019/2/24
 * @modified by:
 */
@Data
public class MongoOutput implements Serializable {
    private String tableId;

    private String userId;

    private Map<String, String> entityData;
}
