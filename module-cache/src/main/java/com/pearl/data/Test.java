package com.pearl.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by lee on 2017/10/16.
 */

public class Test {
    public static void main(String[] args) {
        LoadingCache<String, String> graphs = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<String, String>() {
                            public String load(String key)  {
                                return key + "-value";
                            }
                        });


        try {
            String res = graphs.get("lee", new Callable<String>(){
    
                @Override
                public String call() throws Exception {
                    return "lee" + "callable value";
                }
            });
            System.out.println(res);
        } catch (ExecutionException e) {
            
        }
    }
}
