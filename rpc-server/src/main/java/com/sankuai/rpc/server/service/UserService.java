/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.service;

import com.sankuai.rpc.server.annotation.RemoteService;
import com.sankuai.rpc.server.result.UserInfoDTO;

/**
 * <p>
 *
 * </p>
 * @author fanyuhao
 * @version $Id:UserService.java v1.0 2019/9/30 上午11:18 fanyuhao Exp $
 */
@RemoteService
public interface UserService {

    UserInfoDTO getUserInfoById(Long userId);
}
