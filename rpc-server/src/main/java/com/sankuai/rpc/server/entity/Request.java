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
 * @version $Id:Request.java v1.0 2019/10/17 下午11:38 fanyuhao Exp $
 */
@Data
public class Request {
    private String id;

    /**
     * 类名
     */
    private String interfaceName;

    /**
     * 函数名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] parameters;
}
