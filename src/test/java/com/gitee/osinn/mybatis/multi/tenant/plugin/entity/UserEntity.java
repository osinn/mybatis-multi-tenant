package com.gitee.osinn.mybatis.multi.tenant.plugin.entity;

import java.util.List;

/**
 * 用户表
 *
 * @author wency_cai
 */
public class UserEntity {

    private Long id;

    private String username;

    private String password;

    private Integer tenantId;

    private List<RoleEntity> roleList;

    public UserEntity() {

    }

    public UserEntity(Long id, String username, String password, Integer tenantId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tenantId = tenantId;
    }

//    public UserEntity(Long id, String username, String password) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public List<RoleEntity> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleEntity> roleList) {
        this.roleList = roleList;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tenantId=" + tenantId +
                ", roleList=" + roleList +
                '}';
    }
}
