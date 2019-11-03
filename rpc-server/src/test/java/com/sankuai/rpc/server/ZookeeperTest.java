/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server;

import com.sankuai.common.protocol.CustomZkSerializer;
import com.sankuai.common.utils.JacksonUtils;
import com.sankuai.rpc.server.register.ServiceRegister;
import lombok.extern.log4j.Log4j2;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * <p>
 *  Zookeeper单侧
 * </p>
 * @author fanyuhao
 * @version $Id:ZookeeperTest.java v1.0 2019/11/3 下午3:33 fanyuhao Exp $
 */
@Log4j2
public class ZookeeperTest {
    private ZkClient client;

    {
        this.client = new ZkClient("106.54.242.23:2181", 20000, 20000, CustomZkSerializer.INSTANCE);
    }

    /**
     * 创建zk节点
     */
    @Test
    public void createNode(){

        if(!client.exists("/zkNode")){
            /**
             * PERSISTENT: 节点永久存在，不会随着会话结束而销毁
             * EPHEMERAL：临时节点短暂存在，随着会话结束而销毁
             */
            String zkNode = client.create("/zkNode","zkNode", CreateMode.PERSISTENT);
            log.info("zkNode:{}", zkNode);
        }

        // 查询节点数据
        Object obj = client.readData("/zkNode");

        client.delete("/zkNode");
        Assert.assertEquals(obj, "zkNode");
    }

    /**
     * 获取子节点列表
     */
    @Test
    public void getChildren(){
        List<String> children = client.getChildren("/");
        log.info("children:{}", children);
    }

    /**
     * 读取节点下数据
     */
    @Test
    public void writeData(){

        if(!client.exists("/zkNode")){
            /**
             * PERSISTENT: 节点永久存在，不会随着会话结束而销毁
             * EPHEMERAL：临时节点短暂存在，随着会话结束而销毁
             */
            String zkNode = client.create("/zkNode","zkNode", CreateMode.PERSISTENT);
            log.info("zkNode:{}", zkNode);
        }

        Stat stat = client.writeData("/zkNode", "update zkNode date");
        log.info("stat:{}", JacksonUtils.toJsonString(stat));

        client.delete("/zkNode");
    }

    /**
     * 注册监听事件
     */
    @Test
    public void listen(){

        IZkChildListener listener = (parentPath, childNodes) -> {
            log.info("子节点变更， parentPath:{}, childNodes:{}", parentPath, childNodes);
            return;
        };

        client.subscribeChildChanges("/zkNode", listener);

        if(!client.exists("/zkNode")){
            /**
             * PERSISTENT: 节点永久存在，不会随着会话结束而销毁
             * EPHEMERAL：临时节点短暂存在，随着会话结束而销毁
             */
            String zkNode = client.create("/zkNode","zkNode", CreateMode.PERSISTENT);
            log.info("zkNode:{}", zkNode);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error(e);
        }

    }
}
