package com.zx.security.service.impl;

import com.zx.security.dao.SysUserMapper;
import com.zx.security.entity.SysUser;
import com.zx.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 16:54
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.selectOne(new SysUser().setUsername(username));
    }
}
