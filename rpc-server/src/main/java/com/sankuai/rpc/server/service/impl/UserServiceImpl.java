/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.service.impl;

import com.sankuai.rpc.server.annotation.RemoteService;
import com.sankuai.rpc.server.result.UserInfoDTO;
import com.sankuai.rpc.server.service.UserService;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *  用户中心
 * </p>
 * @author fanyuhao
 * @version $Id:UserServiceImpl.java v1.0 2019/9/30 上午11:18 fanyuhao Exp $
 */
@Log4j2
public class UserServiceImpl implements UserService {

    @Override
    public UserInfoDTO getUserInfoById(Long userId) {
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .name("fanyuhao")
                .sex("man")
                .age(26)
                .build();
        log.info("userInfo:{}", userInfoDTO);
        return userInfoDTO;
    }
}
