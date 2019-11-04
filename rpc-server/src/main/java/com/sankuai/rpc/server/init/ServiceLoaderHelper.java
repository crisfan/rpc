/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.init;

import com.sankuai.common.utils.JacksonUtils;
import com.sankuai.rpc.server.annotation.RemoteService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
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
@Log4j2
public class ServiceLoaderHelper {
    public static final ServiceLoaderHelper INSTANCE = new ServiceLoaderHelper();

    /**
     * key: interfaceName value: instance
     */
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
                serviceMap.put(getInterfaceName(clazz), obj);
            }
        }catch (Throwable t){
            throw new RuntimeException("实例化错误");
        }
    }

    private String getInterfaceName(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        if(Objects.isNull(interfaces) || Objects.equals(interfaces.length, 0 )){
            log.info("类clazz:{}没有实现接口", JacksonUtils.toJsonString(clazz));
            throw new RuntimeException("未实现接口");
        }
        return interfaces[0].getName();
    }

    public Map getServiceMap() {
        return this.serviceMap;
    }

    public static void main(String[] args) {
        ServiceLoaderHelper.INSTANCE.loadService("com.sankuai.rpc.server.service");

        System.out.println(ServiceLoaderHelper.INSTANCE.serviceMap);
    }
}
