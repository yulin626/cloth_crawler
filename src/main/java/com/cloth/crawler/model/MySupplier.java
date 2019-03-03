package com.cloth.crawler.model;

/**
 * @author: YuLin
 * @description:
 * @date: created in 13:41 2019/2/24
 * @modified by:
 */
@FunctionalInterface
public interface MySupplier<T> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws Exception;
}
