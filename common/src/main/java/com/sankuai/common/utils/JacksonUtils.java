/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankuai.common.exception.JacksonParseException;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 * @author fanyuhao
 * @version $Id:JacksonUtils.java v1.0 2019/11/1 下午2:00 fanyuhao Exp $
 */
@Log4j2
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonToObject(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("parse jsonToObject error", e);
            throw new JacksonParseException("解析json出错");
        }
    }

    public static Object byteToObject(byte[] bytes){
        Object obj = null;
        try {
            obj = objectMapper.readValue(bytes, Object.class);
            log.info("obj:{}", JacksonUtils.toJsonString(obj));
        } catch (IOException e) {
            log.error("byteToObject error");
        }
        return obj;

    }

    public static byte[] toJsonBytes(Object obj){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            objectMapper.writeValue(os, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

    public static String toJsonString(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JacksonParseException("转化json出错");
        }
    }
}
