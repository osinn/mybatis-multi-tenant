package com.gitee.osinn.mybatis.multi.tenant.plugin.interceptor;

import org.apache.ibatis.plugin.Invocation;

/**
 * @author wency_cai
 */
public interface TenantPluginInterceptor {


    /**
     * Mybatis 拦截器执行前置处理
     *
     * @param invocation
     */
    default void beforeHandler(Invocation invocation) {

    }

}
