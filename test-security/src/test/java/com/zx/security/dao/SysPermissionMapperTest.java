package com.zx.security.dao;

import com.zx.security.entity.SysPermission;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 16:27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class SysPermissionMapperTest {
    @Autowired
    private SysPermissionMapper permissionMapper;

    @Test
    public void findBySysUserId() throws Exception {
        List<SysPermission> permissionList = permissionMapper.findBySysUserId(1003L);
        permissionList.forEach(item->{
            log.info("权限:{}",item);
        });
    }

}