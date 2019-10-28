/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.netty.hanler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *
 * </p>
 * @author fanyuhao
 * @version $Id:NettyClientHandler.java v1.0 2019/10/28 下午9:28 fanyuhao Exp $
 */
@Log4j2
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    public static NettyClientHandler INSTANCE = new NettyClientHandler();
}
