/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.common.protocol;

import com.sankuai.common.utils.JacksonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *
 * </p>
 * @author fanyuhao
 * @version $Id:JSONDecoder.java v1.0 2019/11/1 下午12:58 fanyuhao Exp $
 */
@Log4j2
public class JSONDecoder extends LengthFieldBasedFrameDecoder {

    public JSONDecoder(){
        super(65535, 0, 4,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        log.info("decode...");
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode==null){
            return null;
        }
        int len = decode.readableBytes();
        byte[] bytes = new byte[len];
        decode.readBytes(bytes);
        return JacksonUtils.byteToObject(bytes);
    }
}
