package com.zx.security.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_permission")
public class SysPermission {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 权限名
     */
    private String name;

    /**
     * url
     */
    private String url;

    /**
     * 备注
     */
    private String description;

    /**
     * 父id
     */
    private Long pid;


}