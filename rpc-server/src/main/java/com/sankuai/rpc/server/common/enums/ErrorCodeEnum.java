/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.common.enums;

import lombok.Getter;

/**
 * <p>
 *  错误码定义
 * </p>
 * @author fanyuhao
 * @version $Id:ErrorCodeEnum.java v1.0 2019/10/18 上午11:26 fanyuhao Exp $
 */
public enum ErrorCodeEnum {
    SUCCESS("0", "成功"),
    SYSTEM_ERROR("9000", "服务器错误");

    @Getter
    private String errorCode;
    @Getter
    private String errorMsg;

    ErrorCodeEnum(String errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
