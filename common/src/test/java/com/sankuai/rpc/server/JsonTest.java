/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.rpc.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankuai.common.utils.JacksonUtils;
import org.junit.Test;

import java.util.HashMap;

/**
 * <p>
 *  json测试
 * </p>
 * @author fanyuhao
 * @version $Id:JsonTest.java v1.0 2019/11/4 下午9:49 fanyuhao Exp $
 */
public class JsonTest {

    private ObjectMapper mapper;

    {
        mapper = new ObjectMapper();
    }

    @Test
    public void testObjectToJson(){

    }
}
