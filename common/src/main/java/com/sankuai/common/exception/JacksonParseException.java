/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.common.exception;

/**
 * <p>
 *  json解析异常
 * </p>
 * @author fanyuhao
 * @version $Id:JacksonParseException.java v1.0 2019/11/1 下午2:08 fanyuhao Exp $
 */
public class JacksonParseException extends RuntimeException{

    public JacksonParseException(String message) {
        super(message);
    }
}
