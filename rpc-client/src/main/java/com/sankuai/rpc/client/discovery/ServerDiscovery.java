/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.discovery;

import com.google.common.collect.Lists;
import com.sankuai.common.utils.JacksonUtils;
import com.sankuai.rpc.client.netty.CustomNettyClient;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;

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

    @Getter
    private ZkClient zkClient;

    private CustomNettyClient nettyClient;

    private volatile boolean isInitialized = false;

    public void init(CustomNettyClient nettyClient){
        if (!isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    this.zkClient = connectServer();
                    this.nettyClient = nettyClient;

                    initConn();
                    watchServer();
                    isInitialized = true;
                }
            }
        }
    }

    /**
     * 初始化连接
     */
    private void initConn() {
        if(!zkClient.exists(ZK_REGISTRY_PATH)){
            log.warn("没有rpc服务器可用!!!");
            return;
        }

        List<String> children = zkClient.getChildren(ZK_REGISTRY_PATH);
        if(CollectionUtils.isNotEmpty(children)){
            List<String> serverAddressList = readData(children);
            nettyClient.updateConn(serverAddressList);
        }
    }

    /**
     * 监听节点变化
     */
    private void watchServer() {
        zkClient.subscribeChildChanges(ZK_REGISTRY_PATH, (parentPath, childrenNodes) -> {
            log.info("监听到变化，parentPath:{}, childrenNodes:{}", parentPath, JacksonUtils.toJsonString(childrenNodes));
            List<String> serverAddressList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(childrenNodes)) {
                log.info("children:{}", JacksonUtils.toJsonString(childrenNodes));
                serverAddressList = readData(childrenNodes);
            }
            nettyClient.updateConn(serverAddressList);
        });
    }

    /**
     * 读取子节点数据(服务端地址)
     * @param children
     */
    private List<String> readData(List<String> children) {
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

    public static void main(String[] args) {

        // 启动netty
        CustomNettyClient.INSTANCE.init();

        // 维护channel连接池
        ServerDiscovery.INSTANCE.init(CustomNettyClient.INSTANCE);
        ServerDiscovery.INSTANCE.watchServer();

        List<Channel> channels = CustomNettyClient.INSTANCE.getChannels();
        log.info("channel:{}", channels);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
