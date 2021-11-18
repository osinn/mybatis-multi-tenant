package com.gitee.osinn.mybatis.multi.tenant.plugin;

import com.gitee.osinn.mybatis.multi.tenant.plugin.handler.TenantInfoHandler;
import com.gitee.osinn.mybatis.multi.tenant.plugin.interceptor.TenantHandlerInterceptor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 共享数据库的多租户系统 MyBatis sql 拦截器
 *
 * @author wency_cai
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class MybatisMultiTenantPluginInterceptor implements Interceptor {


    private TenantHandlerInterceptor tenantHandlerInterceptor;

    public MybatisMultiTenantPluginInterceptor(TenantInfoHandler tenantInfoHandler) {
        this.tenantHandlerInterceptor = new TenantHandlerInterceptor(tenantInfoHandler);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            tenantHandlerInterceptor.beforeHandler(invocation);
        }
        return invocation.proceed();

    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

}
