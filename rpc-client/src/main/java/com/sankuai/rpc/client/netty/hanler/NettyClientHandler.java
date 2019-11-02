/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.netty.hanler;

import com.sankuai.rpc.client.Exception.NotUsedConnException;
import com.sankuai.rpc.client.netty.CustomNettyClient;
import com.sankuai.rpc.server.common.utils.JacksonUtils;
import com.sankuai.rpc.server.entity.Request;
import com.sankuai.rpc.server.entity.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
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

    /**
     * key: 唯一id value: queue
     */
    private ConcurrentHashMap<String, SynchronousQueue<Response>> queueMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(Objects.nonNull(msg)){
            Response response = JacksonUtils.jsonToObject(msg.toString(), Response.class);
            SynchronousQueue<Response> queue = queueMap.get(response.getRequestId());
            queue.put(response);
            queueMap.remove(response.getRequestId());
        }
    }

    /**
     * 发送请求
     * @param request
     */
    public SynchronousQueue<Response> sendRequest(Request request){

        System.out.println("发送请求");
        List<Channel> channelList = CustomNettyClient.INSTANCE.getChannels();
        if(CollectionUtils.isEmpty(channelList)){
            log.error("没有可用的连接");
            throw new NotUsedConnException("没有可用的连接");
        }

        // TODO: 优化选取channel策略
        Random random = new Random();
        int next = random.nextInt(channelList.size());

        Channel channel = channelList.get(next);
        log.info("channel", com.sankuai.rpc.client.common.utils.JacksonUtils.toJsonString(channel));
        if(Objects.nonNull(channel) && channel.isActive()){
            log.info("request:{}", com.sankuai.rpc.client.common.utils.JacksonUtils.toJsonString(request));
            channel.writeAndFlush(request);
        }

        SynchronousQueue<Response> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        return queue;
    }
}
