package com.gitee.osinn.mybatis.multi.tenant.plugin.entity;

/**
 * 描述
 *
 * @author wency_cai
 */
public class RoleEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    private Integer tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
