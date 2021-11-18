package com.gitee.osinn.mybatis.multi.tenant.plugin.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户参数配置
 *
 * @author wency_cai
 */
@ConfigurationProperties(prefix = TenantProperties.PREFIX)
public class TenantProperties {

    public final static String PREFIX = "mybatis.tenant.config";

    /**
     * 数据库中租户ID的列名
     */
    private String tenantIdColumn = "tenant_id";

    /**
     * 是否忽略表按租户ID过滤,默认所有表都按租户ID过滤，指定表名称或表前缀
     */
    private List<String> ignoreTablePrefix = new ArrayList<>();

    /**
     * 是否开启多租户配置
     */
    private boolean enable;

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public List<String> getIgnoreTablePrefix() {
        return ignoreTablePrefix;
    }

    public void setIgnoreTablePrefix(List<String> ignoreTablePrefix) {
        this.ignoreTablePrefix = ignoreTablePrefix;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
