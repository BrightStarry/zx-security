package com.zx.security.service;

import com.zx.security.entity.SysUser;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 16:53
 */
public interface UserService {
    SysUser findByUsername(String username);
}
