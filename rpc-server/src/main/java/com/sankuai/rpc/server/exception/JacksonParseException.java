/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.exception;

/**
 * <p>
 *  Jackson解析错误
 * </p>
 * @author fanyuhao
 * @version $Id:JacksonParseException.java v1.0 2019/10/18 上午10:31 fanyuhao Exp $
 */
public class JacksonParseException extends RuntimeException {

    public JacksonParseException(String message) {
        super(message);
    }
}
