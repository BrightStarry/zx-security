package com.zx.security.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 11:32
 */
public interface CommonMapper<T> extends Mapper<T>,MySqlMapper<T> {
}
