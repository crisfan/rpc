/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.register;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *  服务注册器（基于zookeeper）
 * </p>
 * @author fanyuhao
 * @version $Id:ServiceRegister.java v1.0 2019/10/18 下午12:00 fanyuhao Exp $
 */
@Log4j2
public class ServiceRegister {

    public static ServiceRegister INSTANCE = new ServiceRegister();

    private AtomicBoolean start = new AtomicBoolean(false);

    @Getter
    private String registryAddress = "106.54.242.23:2181";

    @Getter
    private final String ZK_REGISTRY_PATH = "/rpc";

    @Getter
    private ZkClient client;

    private ServiceRegister(){}

    public void register(String data) {
        if(!start.get()){
            client = connectServer();
            if (client != null) {
                addRootNode(client);
                createNode(client, data);
            }
            start.compareAndSet(false, true);
        }
    }

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress,20000,20000);
        log.info("连接zookeeper.....，address:{}", registryAddress);
        return client;
    }

    private void addRootNode(ZkClient client) {
        boolean exists = client.exists(ZK_REGISTRY_PATH);
        if (!exists) {
            client.createPersistent(ZK_REGISTRY_PATH);
            log.info("创建zookeeper主节点 {}", ZK_REGISTRY_PATH);
        }
    }

    private void createNode(ZkClient client, String data) {
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("创建zookeeper数据节点 => path:{},data:{})", path, data);
    }

    public static void main(String[] args) {
        ServiceRegister.INSTANCE.register("127.0.0.1:9000");

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
