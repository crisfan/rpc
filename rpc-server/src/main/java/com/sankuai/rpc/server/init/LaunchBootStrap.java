/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.init;

import com.sankuai.rpc.server.netty.CustomNettyServer;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *  启动类
 * </p>
 * @author fanyuhao
 * @version $Id:LaunchBootStrap.java v1.0 2019/9/30 下午3:51 fanyuhao Exp $
 */
@Log4j2
public class LaunchBootStrap{
    public static String packageName = "com.sankuai.rpc.server.service";

    public static void main(String[] args) {
        log.info("start...");

        // 加载提供的服务
        ServiceLoaderHelper.INSTANCE.loadService(packageName);

        // 服务器启动
        CustomNettyServer.INSTANCE.launch();
    }
}
