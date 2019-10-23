/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.init;

import com.sankuai.rpc.server.annotation.RemoteService;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  服务加载器
 * </p>
 * @author fanyuhao
 * @version $Id:ServiceLoaderHelper.java v1.0 2019/10/17 下午11:31 fanyuhao Exp $
 */
public class ServiceLoaderHelper {
    public static final ServiceLoaderHelper INSTANCE = new ServiceLoaderHelper();

    private ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 加载指定包下的类
     * @param packageName
     */
    public void loadService(String packageName) {
        if (Objects.isNull(packageName)) {
            return;
        }

        try {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RemoteService.class);
            for (Class clazz: classes){
                if(clazz.isInterface()){
                    continue;
                }
                Object obj = clazz.newInstance();
                serviceMap.put(clazz.getName(), obj);
            }
        }catch (Throwable t){
            throw new RuntimeException("实例化错误");
        }
    }

    public Map getServiceMap() {
        return this.serviceMap;
    }
}
