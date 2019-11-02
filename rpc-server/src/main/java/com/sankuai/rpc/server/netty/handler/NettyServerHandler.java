/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.netty.handler;

import com.sankuai.common.utils.JacksonUtils;
import com.sankuai.rpc.server.common.enums.ErrorCodeEnum;
import com.sankuai.rpc.server.entity.Request;
import com.sankuai.rpc.server.entity.Response;
import com.sankuai.rpc.server.init.ServiceLoaderHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  请求处理器
 * </p>
 * @author fanyuhao
 * @version $Id:NettyServerHandler.java v1.0 2019/10/17 下午11:12 fanyuhao Exp $
 */
@Log4j2
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx)   {
        log.info("客户端连接成功!"+ ctx.channel().remoteAddress());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("收到消息:{}", JacksonUtils.toJsonString(msg));
        Object result = null;
        Request request = JacksonUtils.jsonToObject(msg.toString(), Request.class);
        log.info("result:{}", JacksonUtils.toJsonString(result));
        try {
            Map serviceMap = ServiceLoaderHelper.INSTANCE.getServiceMap();
            log.info("serviceMap:{}", JacksonUtils.toJsonString(serviceMap));
            Object serviceBean = serviceMap.get(request.getInterfaceName());
            if(Objects.nonNull(serviceBean)){
                Method method = getMethod(request, serviceBean);
                result = method.invoke(serviceBean, getParameters(request.getParameterTypes(), request.getParameters()));
            }
            log.info("result:{}", JacksonUtils.toJsonString(request));
            Response response = setResponse(request.getId(), result, true);
            ctx.writeAndFlush(response);
        }catch (Throwable t){
            log.error("channel read error", t);
            setResponse(request.getId(), result, false);
            ctx.writeAndFlush(result);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("客户端已超过60秒未读写数据,关闭连接.{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private Response setResponse(String id, Object result, boolean success) {
        ErrorCodeEnum codeEnum = success ? ErrorCodeEnum.SUCCESS : ErrorCodeEnum.SYSTEM_ERROR;

        Response response = new Response();
        response.setRequestId(id);
        response.setCode(codeEnum.getErrorCode());
        response.setMsg(codeEnum.getErrorMsg());
        response.setData(result);
        return response;
    }

    private Method getMethod(Request request, Object serviceBean) throws NoSuchMethodException {
        return serviceBean.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
    }

    private Object getParameters(Class<?>[] parameterTypes, Object[] parameters){
        if(Objects.isNull(parameters) || parameters.length == 0){
            return parameters;
        }

        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            objects[i] = JacksonUtils.jsonToObject(parameters[i].toString(), parameterTypes[i]);
        }
        return objects;
    }
}
