/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.entity;

import lombok.Data;

/**
 * <p>
 *  请求定义
 * </p>
 * @author fanyuhao
 * @version $Id:Response.java v1.0 2019/10/17 下午11:38 fanyuhao Exp $
 */
@Data
public class Response {
    /**
     * 请求号
     */
    private String requestId;

    /**
     * 返回码
     */
    private String code;

    /**
     * 错误描述
     */
    private String error_msg;

    /**
     * 数据
     */
    private Object data;
}
