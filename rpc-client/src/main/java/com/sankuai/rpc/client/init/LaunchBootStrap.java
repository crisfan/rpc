/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.init;

import com.sankuai.rpc.client.discovery.ServerDiscovery;
import com.sankuai.rpc.client.netty.CustomNettyClient;
import com.sankuai.rpc.client.proxy.ProxyHandler;
import com.sankuai.rpc.server.result.UserInfoDTO;
import com.sankuai.rpc.server.service.UserService;

/**
 * <p>
 *  客户端启动程序
 * </p>
 * @author fanyuhao
 * @version $Id:LaunchBootStrap.java v1.0 2019/10/24 下午10:31 fanyuhao Exp $
 */
public class LaunchBootStrap {

    public static String packageName = "com.sankuai.rpc.server.service";

    public static void main(String[] args) {

        // 代理将要访问的服务
        ProxyHandler.INSTANCE.init(packageName);

        // 初始化客户端
        CustomNettyClient.INSTANCE.init();

        // 监听server端
        ServerDiscovery.INSTANCE.init();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        UserService userService = ProxyHandler.INSTANCE.getProxyInstance(UserService.class);
        UserInfoDTO user = userService.getUserInfoById(1L);
        System.out.println(user);

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
