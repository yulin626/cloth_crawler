package com.cloth.crawler.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author: YuLin
 * @description:
 * @date: created in 13:39 2019/2/24
 * @modified by:
 */
@Component
@Scope("singleton")
@Slf4j
public class ResultBundleBuilder {
    public <T> ResultBundle<T> bundle(String keyword, MySupplier<T> supplier) {
        ResultBundle<T> resultBundle;
        long start = System.currentTimeMillis();
        try {
            T t = supplier.get();
            resultBundle = new ResultBundle<>(t, keyword, System.currentTimeMillis() - start);
        } catch (Exception e) {
            resultBundle = new ResultBundle<>(keyword, System.currentTimeMillis() - start, false, e.getClass().getName() + ":" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return resultBundle;
    }

    public <T> ResultListBundle<T> listBundle(String keyword, MySupplier<? extends Collection<T>> supplier) {
        ResultListBundle<T> resultBundle;
        long start = System.currentTimeMillis();
        try {
            Collection<T> t = supplier.get();
            resultBundle = new ResultListBundle<>(t, keyword, System.currentTimeMillis() - start);
        } catch (Exception e) {
            resultBundle = new ResultListBundle<>(keyword, System.currentTimeMillis() - start, false, e.getClass().getName() + ":" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return resultBundle;
    }
}
