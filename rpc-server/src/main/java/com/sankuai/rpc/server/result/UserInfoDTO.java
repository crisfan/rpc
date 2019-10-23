/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.result;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * <p>
 *  用户基本信息定义
 * </p>
 * @author fanyuhao
 * @version $Id:UserInfoDTO.java v1.0 2019/9/30 上午11:21 fanyuhao Exp $
 */

@Builder
@Getter
public class UserInfoDTO {

    private String name;

    private Integer age;

    private String sex;
}
