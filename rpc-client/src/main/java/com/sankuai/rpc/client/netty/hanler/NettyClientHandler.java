/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.netty.hanler;

import com.sankuai.rpc.client.Exception.NotUsedConnException;
import com.sankuai.rpc.client.netty.CustomNettyClient;
import com.sankuai.rpc.server.entity.Request;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * <p>
 *  请求发送&请求接收处理器
 * </p>
 * @author fanyuhao
 * @version $Id:NettyClientHandler.java v1.0 2019/10/28 下午9:28 fanyuhao Exp $
 */
@Log4j2
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    public static NettyClientHandler INSTANCE = new NettyClientHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * 发送请求
     * @param request
     */
    public SynchronousQueue sendRequest(Request request){
        List<Channel> channelList = CustomNettyClient.INSTANCE.getChannels();
        if(CollectionUtils.isEmpty(channelList)){
            log.error("没有可用的连接");
            throw new NotUsedConnException("没有可用的连接");
        }

        // TODO: 优化选取channel策略
        Random random = new Random();
        int next = random.nextInt(channelList.size());

        Channel channel = channelList.get(next);
        if(Objects.nonNull(channel) && channel.isActive()){
            channel.writeAndFlush(request);
        }

        SynchronousQueue queue = new SynchronousQueue();
        return queue;
    }
}
