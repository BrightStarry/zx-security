package com.zx.security.security;

import com.zx.security.dao.SysPermissionMapper;
import com.zx.security.dao.SysUserMapper;
import com.zx.security.entity.SysPermission;
import com.zx.security.entity.SysUser;
import com.zx.security.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 16:41
 * 自定义用户详情服务类，实现security的接口
 */
@Service
@Slf4j
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private SysPermissionMapper permissionMapper;

    /**
     * 根据用户名加载用户详情的方法,需要查询并注入用户所有权限，然后返回security定义的User对象
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //根据用户名查询用户
        @NonNull SysUser sysUser = userService.findByUsername(s);

        //根据用户查询所有权限
        List<SysPermission> permissionList = permissionMapper.findBySysUserId(sysUser.getId());

        //GrantedAuthorityList
        List<GrantedAuthority> grantedAuthoritieList = new ArrayList<>();

        //将所有权限对象封装为SimpleGrantedAuthority然后存入grantedAuthoritieList
        //SimpleGrantedAuthority对象应该存的是角色，此处存的是权限名
        permissionList.parallelStream().forEach(item->{
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(item.getName());
            grantedAuthoritieList.add(grantedAuthority);
        });

        return new CustomUser(sysUser.getUsername(), sysUser.getPassword(), sysUser.getPhone(), grantedAuthoritieList);
    }
}
