/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.discovery;

import com.google.common.collect.Lists;
import com.sankuai.rpc.client.netty.CustomNettyClient;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务发现
 * </p>
 * @author fanyuhao
 * @version $Id:ServerDiscovery.java v1.0 2019/10/24 下午11:16 fanyuhao Exp $
 */
@Log4j2
public class ServerDiscovery {

    public static ServerDiscovery INSTANCE = new ServerDiscovery();

    @Getter
    private String registryAddress = "106.54.242.23:2181";

    @Getter
    private final String ZK_REGISTRY_PATH = "/rpc";

    private volatile boolean isInitialized = false;
    
    private ZkClient zkClient;

    public void init(){
        if (!isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    zkClient = connectServer();
                    watch();
                    isInitialized = true;
                }
            }
        }
    }

    /**
     * 监听节点变化
     */
    private void watch() {
        zkClient.subscribeChildChanges(ZK_REGISTRY_PATH, (parentPath, children) -> {
            if (children == null) {
                log.info("<" + parentPath + "> is deleted");
                return;
            }

            List<String> serverAddressList = read(children);
            CustomNettyClient.INSTANCE.updateConn(serverAddressList);
        });
    }

    /**
     * 读取子节点数据(服务端地址)
     * @param children
     */
    private List<String> read(List<String> children) {
        ArrayList<String> serverAddressList = Lists.newArrayList();
        for (String path: children){
            String serverAddress = zkClient.readData(ZK_REGISTRY_PATH + "/" + path);
            serverAddressList.add(serverAddress);
        }
        return serverAddressList;
    }

    /**
     * 连接zk服务器
     */
    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress,20000,20000);
        log.info("连接zookeeper.....，address:{}", registryAddress);
        return client;
    }

}
