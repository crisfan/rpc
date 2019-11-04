/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.service.impl;

import com.sankuai.common.utils.JacksonUtils;
import com.sankuai.rpc.server.entity.Request;
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

    public static void main(String[] args) {

        Request request = new Request();
        request.setId("1");
        request.setInterfaceName("UserService");
        request.setParameters(new Object[]{1, 2});

        Object json = JacksonUtils.toJsonString(request);
        System.out.println(json.toString());

        Request object = JacksonUtils.jsonToObject(json.toString(), Request.class);
        System.out.println(object);

    }

}
