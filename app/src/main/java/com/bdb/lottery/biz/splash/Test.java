package com.bdb.lottery.biz.splash;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

/**
 * @Description:
 * @Author: orange
 * @Date: 2020/10/22 5:49 PM
 */
public class Test {
    public static void main(String[] args) {
        Observable.fromArray("1")
                .onErrorReturn(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) throws Throwable {
                        return null;
                    }
                })
                .subscribe();
    }
}
