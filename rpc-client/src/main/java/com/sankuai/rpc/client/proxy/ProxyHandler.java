/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.proxy;

import com.sankuai.rpc.client.netty.hanler.NettyClientHandler;
import com.sankuai.rpc.server.annotation.RemoteService;
import com.sankuai.rpc.server.entity.Request;
import org.reflections.Reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  代理即将远程调用的接口
 * </p>
 * @author fanyuhao
 * @version $Id:ProxyHandler.java v1.0 2019/10/24 下午9:57 fanyuhao Exp $
 */
public class ProxyHandler {
    /**
     * key: 接口全限定名称 value: 该接口的代理类
     */
    private Map<String, Object> proxyMap;

    public static ProxyHandler INSTANCE = new ProxyHandler();

    private ProxyHandler(){
        proxyMap = new ConcurrentHashMap<>();
    }

    /**
     * 动态代理生成所有即将远程调用接口的代理类，并存放到proxyMap中
     */
    public void init(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RemoteService.class);

        for (Class clazz: classes){
            if(!clazz.isInterface()){
                continue;
            }

            InvocationHandler handler = getHandler();
            Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);
            proxyMap.put(clazz.getName(), proxyInstance);
        }
    }

    private InvocationHandler getHandler(){
        return (proxy, method, args) -> {
            Request request = getRequest(proxy, method, args);
            NettyClientHandler.INSTANCE.sendRequest(request);
            return null;
        };
    }

    private Request getRequest(Object proxy, Method method, Object[] args) {
        Request request = new Request();
        request.setClassName(proxy.getClass().getSimpleName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        return request;
    }

    public <T> T getProxyInstance(Class<T> clazz){
        Object proxy = proxyMap.get(clazz.getName());

        if(Objects.nonNull(proxy) && clazz.isInstance(proxy)){
            return (T) proxy;
        }

        throw new RuntimeException("can't find clazz");
    }
}
