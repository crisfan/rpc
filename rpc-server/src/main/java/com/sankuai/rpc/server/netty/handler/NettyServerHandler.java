/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.netty.handler;

import com.sankuai.rpc.server.common.enums.ErrorCodeEnum;
import com.sankuai.rpc.server.entity.Request;
import com.sankuai.rpc.server.entity.Response;
import com.sankuai.rpc.server.init.ServiceLoaderHelper;
import com.sankuai.rpc.server.common.utils.JacksonUtils;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = new Response();
        Object result = null;
        try {
            Request request = JacksonUtils.jsonToObject(msg.toString(), Request.class);

            Map serviceMap = ServiceLoaderHelper.INSTANCE.getServiceMap();
            String fullQualifiedName = getFullQualifiedName(request.getClassName(), request.getMethodName());

            Object serviceBean = serviceMap.get(fullQualifiedName);
            if(Objects.nonNull(serviceBean)){
                Method method = getMethod(request, serviceBean);
                result = method.invoke(serviceBean, getParameters(request.getParameterTypes(), request.getParameters()));
            }

            setResponse(response, ErrorCodeEnum.SUCCESS, result);
            ctx.writeAndFlush(response);
        }catch (Throwable t){
            log.error("channel read error", t);
            setResponse(response, ErrorCodeEnum.SYSTEM_ERROR, result);
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

    private Response setResponse(Response response, ErrorCodeEnum codeEnum, Object result) {
        response.setCode(codeEnum.getErrorCode());
        response.setError_msg(codeEnum.getErrorMsg());
        response.setData(result);
        return response;
    }

    private Method getMethod(Request request, Object serviceBean) throws NoSuchMethodException {
        return serviceBean.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
    }

    private String getFullQualifiedName(String clazzName, String method){
        return clazzName + "." + method;
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
