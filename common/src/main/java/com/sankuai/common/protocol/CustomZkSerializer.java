/**
 * meituan.com Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */

package com.sankuai.common.protocol;

import com.sankuai.common.utils.JacksonUtils;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/**
 * <p>
 *  zk序列化插件
 * </p>
 * @author fanyuhao
 * @version $Id:CustomZkSerializer.java v1.0 2019/11/3 下午3:39 fanyuhao Exp $
 */
public class CustomZkSerializer implements ZkSerializer {
    public static CustomZkSerializer INSTANCE = new CustomZkSerializer();


    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        return JacksonUtils.toJsonBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return JacksonUtils.byteToObject(bytes);
    }
}
