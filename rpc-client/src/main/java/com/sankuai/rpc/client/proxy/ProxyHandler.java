/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.proxy;

import com.sankuai.rpc.client.netty.hanler.NettyClientHandler;
import com.sankuai.rpc.server.annotation.RemoteService;
import com.sankuai.rpc.server.entity.Request;
import com.sankuai.rpc.server.entity.Response;
import org.reflections.Reflections;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

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

            InvocationHandler handler = getHandler(clazz.getName());
            Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);
            proxyMap.put(clazz.getName(), proxyInstance);
        }
    }

    private InvocationHandler getHandler(String interfaceName){
        return (proxy, method, args) -> {
            Request request = getRequest(interfaceName, method, args);
            SynchronousQueue<Response> queue = NettyClientHandler.INSTANCE.sendRequest(request);
            return queue.take();
        };
    }

    private Request getRequest(String interfaceName, Method method, Object[] args) {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceName(interfaceName);
        request.setMethodName(method.getName());
        request.setParameters(args);

        if(args.length != 0){
            Class[] clazz = new Class[args.length];
            for (int i=0; i < args.length; i++){
                clazz[i] = args[i].getClass();
            }
            request.setParameterTypes(clazz);
        }
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
