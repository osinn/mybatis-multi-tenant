package com.gitee.osinn.mybatis.multi.tenant.plugin.interceptor;

import com.gitee.osinn.mybatis.multi.tenant.plugin.handler.TenantInfoHandler;
import com.gitee.osinn.mybatis.multi.tenant.plugin.parser.DefaultSqlParser;
import com.gitee.osinn.mybatis.multi.tenant.plugin.parser.SqlParser;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拦截器处理器
 *
 * @author wency_cai
 */
public class TenantHandlerInterceptor implements TenantPluginInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantHandlerInterceptor.class);

    /**
     * 处理多租户接口
     */
    private final TenantInfoHandler tenantInfoHandler;

    /**
     * sql解析器
     */
    private final SqlParser sqlParser;

    public TenantHandlerInterceptor(TenantInfoHandler tenantInfoHandler) {
        this.sqlParser = new DefaultSqlParser(tenantInfoHandler);
        this.tenantInfoHandler = tenantInfoHandler;
    }

    @Override
    public void beforeHandler(Invocation invocation) {
        long start = System.currentTimeMillis();

        this.mod(invocation);

        long end = System.currentTimeMillis();
        long time = end - start;

        logger.debug("多租户SQL解析耗时：{} ms", time);
    }


    /**
     * 更改MappedStatement为新的
     *
     * @param invocation
     */
    private void mod(Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];

        BoundSql boundSql = ms.getBoundSql(invocation.getArgs()[1]);

        //根据方法忽略多租户字段
        if (TenantInfoHandler.IGNORE_TENANT_ID_METHODS.contains(ms.getId())) {
            return;
        }
        /**
         * 根据已有BoundSql构造新的BoundSql
         *
         */
        String sql = boundSql.getSql();
        // 如果已经添加过租户字段不再处理
//        if (sql.contains(tenantInfoHandler.getTenantIdColumn())) {
//            return;
//        }

        //更改后的sql
        sql = sqlParser.setTenantParameter(boundSql.getSql());
        BoundSql newBoundSql = new BoundSql(
                ms.getConfiguration(),
                sql,
                boundSql.getParameterMappings(),
                boundSql.getParameterObject());
        MappedStatement newMs = buildMappedStatement(ms, new TenantHandlerInterceptor.BoundSqlSqlSource(newBoundSql));

        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        /**
         * 替换 MappedStatement
         */
        invocation.getArgs()[0] = newMs;

    }

    /**
     * 根据已有MappedStatement构造新的MappedStatement
     */
    private MappedStatement buildMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


    /**
     * 用于构造新MappedStatement
     */
    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}


