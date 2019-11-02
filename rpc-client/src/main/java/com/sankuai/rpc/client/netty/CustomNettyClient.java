/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.netty;

import com.google.common.collect.Lists;
import com.sankuai.common.protocol.JSONEncoder;
import com.sankuai.rpc.client.common.utils.JacksonUtils;
import com.sankuai.rpc.client.netty.hanler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  客户端
 * </p>
 * @author fanyuhao
 * @version $Id:CustomNettyClient.java v1.0 2019/10/27 下午11:54 fanyuhao Exp $
 */
@Log4j2
public class CustomNettyClient {

    public static CustomNettyClient INSTANCE = new CustomNettyClient();

    private ConcurrentHashMap<SocketAddress, Channel> channelMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private volatile boolean isInitialized = false;

    public void init() {
        if (!isInitialized) {
            synchronized (this) {
                if (!isInitialized) {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(new NioEventLoopGroup(1)).
                            channel(NioSocketChannel.class).
                            option(ChannelOption.TCP_NODELAY, true).
                            option(ChannelOption.SO_KEEPALIVE, true).
                            handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel channel) throws Exception {
                                    ChannelPipeline pipeline = channel.pipeline();
                                    pipeline.addLast(new JSONEncoder());
                                    pipeline.addLast(new IdleStateHandler(0, 0, 30));
                                    pipeline.addLast("handler", NettyClientHandler.INSTANCE);
                                }
                            });
                    this.bootstrap = bootstrap;
                    isInitialized = true;
                }
            }
        }
    }

    /**
     * 更新可连接的channel
     * @param serverAddressList
     */
    public synchronized void updateConn(List<String> serverAddressList){

        if(CollectionUtils.isEmpty(serverAddressList)){
            channelMap.clear();
        }

        // 新增新的server连接
        List<SocketAddress> newSocketAddressList = Lists.newArrayList();
        for (String serverAddress: serverAddressList) {
            String[] ipAndPort = serverAddress.split(":");
            if(!Objects.equals(ipAndPort.length, 2)){
                continue;
            }

            SocketAddress socketAddress = new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            Channel channel = channelMap.get(socketAddress);
            if(Objects.isNull(channel) || !channel.isOpen()){
                channelMap.put(socketAddress, createChannel(socketAddress));
            }
            newSocketAddressList.add(socketAddress);
        }

        // 移除已经被下线的server
        Iterator<Map.Entry<SocketAddress, Channel>> it = channelMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<SocketAddress, Channel> next = it.next();
            SocketAddress oldSocketAddress = next.getKey();
            if(!newSocketAddressList.contains(oldSocketAddress)){
               it.remove();
            }
        }
    }

    private Channel createChannel(SocketAddress socketAddress) {
        Channel channel = null;
        try {
            ChannelFuture channelFuture = bootstrap.connect(socketAddress);
            channel = channelFuture.sync().channel();
        } catch (Exception e) {
            log.error("连接远程服务器失败，socketAddress:{}", JacksonUtils.toJsonString(socketAddress), e);
        }
        return channel;
    }

    public List<Channel> getChannels(){
        List<Channel> channelList = Lists.newArrayList();
        if(MapUtils.isNotEmpty(channelMap)){
            Set<Map.Entry<SocketAddress, Channel>> entries = channelMap.entrySet();
            for (Map.Entry<SocketAddress, Channel> entry: entries){
                channelList.add(entry.getValue());
            }
        }
        return channelList;
    }

    public static void main(String[] args) {
        List<String> serverAddressList = Lists.newArrayList();
        serverAddressList.add("127.0.0.1:8080");
        CustomNettyClient.INSTANCE.updateConn(serverAddressList);
    }
}
