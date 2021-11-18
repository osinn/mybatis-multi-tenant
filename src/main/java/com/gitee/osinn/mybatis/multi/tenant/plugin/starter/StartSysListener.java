package com.gitee.osinn.mybatis.multi.tenant.plugin.starter;

import com.gitee.osinn.mybatis.multi.tenant.plugin.MybatisMultiTenantPluginInterceptor;
import com.gitee.osinn.mybatis.multi.tenant.plugin.handler.TenantInfoHandler;
import com.gitee.osinn.mybatis.multi.tenant.plugin.service.ITenantService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * @author wency_cai
 */
public class StartSysListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger log = LoggerFactory.getLogger(StartSysListener.class);

    private List<SqlSessionFactory> sqlSessionFactoryList;

    private ITenantService tenantService;

    private TenantProperties tenantProperties;

    public StartSysListener(ITenantService tenantService, TenantProperties tenantProperties, List<SqlSessionFactory> sqlSessionFactoryList) {
        this.tenantService = tenantService;
        this.tenantProperties = tenantProperties;
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("添加自定义Mybatis多租户SQL拦截器");
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            // 添加拦截器
            sqlSessionFactory.getConfiguration().addInterceptor(new MybatisMultiTenantPluginInterceptor(new TenantInfoHandler() {

                @Override
                public List<Object> getTenantIds() {
                    return tenantService.getTenantIds();
                }

                @Override
                public List<String> ignoreTablePrefix() {
                    return tenantProperties.getIgnoreTablePrefix();
                }

                @Override
                public String getTenantIdColumn() {
                    return tenantProperties.getTenantIdColumn();
                }
            }));
        }
    }
}