package com.gitee.osinn.mybatis.multi.tenant.plugin.entity;

/**
 * 描述
 *
 * @author wency_cai
 */
public class UserRoleEntity {

    private Long id;

    private Long userId;

    private Long roleId;

    private Integer tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "UserRoleEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", tenantId=" + tenantId +
                '}';
    }
}
