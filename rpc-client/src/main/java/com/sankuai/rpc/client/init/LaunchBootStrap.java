/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.init;

import com.sankuai.rpc.client.proxy.ProxyHandler;

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

        ProxyHandler.INSTANCE.init(packageName);




    }
}
