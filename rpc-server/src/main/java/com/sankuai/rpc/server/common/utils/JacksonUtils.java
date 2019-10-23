/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankuai.rpc.server.exception.JacksonParseException;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 *  Jackson工具类
 * </p>
 * @author fanyuhao
 * @version $Id:JacksonUtils.java v1.0 2019/10/18 上午9:55 fanyuhao Exp $
 */
@Log4j2
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonToObject(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
           throw new JacksonParseException("解析json出错");
        }
    }
}
