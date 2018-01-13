package com.zx.security.dao;

import com.zx.security.config.CommonMapper;
import com.zx.security.entity.SysPermission;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysPermissionMapper extends CommonMapper<SysPermission> {

    /**
     * 根据用户id查询所有权限
     */
    //不考虑父id的查询
//    @Select("select sp.id,sp.name,sp.url,sp.description,sp.pid " +
//            "from sys_user su " +
//            "left join sys_user_role sur on su.id = sur.sys_user_id " +
//            "left join sys_role_permission srp on sur.sys_role_id = srp.sys_role_id " +
//            "left join sys_permission sp on srp.sys_permission_id = sp.id " +
//            "where su.id = #{userId}")
    @Select("select sp.id,sp.name,sp.url,sp.description,sp.pid " +
            "from sys_user su " +
            "left join sys_user_role sur on su.id = sur.sys_user_id " +
            "left join sys_role_permission srp on sur.sys_role_id = srp.sys_role_id " +
            "left join sys_permission sp on srp.sys_permission_id = sp.id " +
            "where su.id = #{userId}")
    List<SysPermission> findBySysUserId(Long userId);
}