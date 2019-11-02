/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.netty;

import com.fasterxml.jackson.core.JsonEncoding;
import com.sankuai.common.protocol.JSONDecoder;
import com.sankuai.common.protocol.JSONEncoder;
import com.sankuai.rpc.server.register.ServiceRegister;
import com.sankuai.rpc.server.netty.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *  服务端
 * </p>
 * @author fanyuhao
 * @version $Id:CustomNettyServer.java v1.0 2019/9/30 下午1:21 fanyuhao Exp $
 */
@Log4j2
public class CustomNettyServer {

    public static CustomNettyServer INSTANCE = new CustomNettyServer();

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final int port = 9000;

    private final String host = "127.0.0.1";

    private CustomNettyServer(){

    }

    public void launch() {
        if(!started.get()){
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup,workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG,1024)
                        .childOption(ChannelOption.SO_KEEPALIVE,true)
                        .childOption(ChannelOption.TCP_NODELAY,true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                ChannelPipeline pipeline = channel.pipeline();
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new NettyServerHandler());
                            }
                        });
                ChannelFuture cf = bootstrap.bind(host, port).sync();
                log.info("netty服务器启动.监听端口:" + host + ":" + port);
                ServiceRegister.INSTANCE.register(getServerAddress());
                log.info("服务注册成功");
                cf.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("启动失败", e);
            }finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

    public String getServerAddress(){
        return host + ":" + port;
    }
}
