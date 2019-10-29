/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.client.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankuai.rpc.server.exception.JacksonParseException;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *
 * </p>
 * @author fanyuhao
 * @version $Id:JacksonUtils.java v1.0 2019/10/29 下午9:56 fanyuhao Exp $
 */
@Log4j2
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JacksonParseException("转化json出错");
        }
    }
}
