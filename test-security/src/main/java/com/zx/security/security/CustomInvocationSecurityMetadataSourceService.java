package com.zx.security.security;

import com.zx.security.dao.SysPermissionMapper;
import com.zx.security.entity.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 17:19
 *
 * 重写security加载权限元数据的接口
 */
@Component
public class CustomInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private SysPermissionMapper permissionMapper;

    //存储权限的map
    private Map<String,Collection<ConfigAttribute>> permissionUrlMap;
    //存储权限url的匹配起的list，此处是为了下面更快的匹配用户请求url是否在权限url中,空间换取时间
    private List<AntPathRequestMatcher> urlMatcherList;

    /**
     * 加载表中权限到map
     */
    private void loadResource(){
        //使用线程安全的map,
        permissionUrlMap = new ConcurrentHashMap<>();
        //线程安全的，并支持读多写少的并发list
        urlMatcherList = new CopyOnWriteArrayList<>();
        //查询所有权限
        List<SysPermission> permissionList = permissionMapper.selectAll();

        //遍历权限集合，将权限url为key，权限的name作为值，存入map
        permissionList.parallelStream().forEach(permission->{
            //每个权限的一些配置属性集合,除了权限的名字，还可以存放其他权限信息
            Collection<ConfigAttribute> configAttributes= new ArrayList<>();
            configAttributes.add(new SecurityConfig(permission.getName()));
            //存入两个集合
            permissionUrlMap.put(permission.getUrl(),configAttributes);
            //匹配器，用来将用户访问url和权限中的url匹配，ps:权限url可能是/user/**形式的，所以用它
            urlMatcherList.add(new AntPathRequestMatcher(permission.getUrl()));
        });
    }


    /**
     * 判断用户请求url是否在权限map中，如果在，返回权限信息给decide()，否则放行，返回null
     * @param o
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        //这么改。。。安全些
        //如果map为空，就加载map
        if(permissionUrlMap == null){
            synchronized (CustomInvocationSecurityMetadataSourceService.class){
                if(permissionUrlMap == null)
                    loadResource();
            }
        }

        //将Object转为FilterInvocation，取出request
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        //遍历匹配器集合,如果匹配，就返回匹配到的权限信息
        for (AntPathRequestMatcher matcher : urlMatcherList) {
            if (matcher.matches(request))
                return permissionUrlMap.get(matcher.getPattern());
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    //此处需要修改为true
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
