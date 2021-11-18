package com.gitee.osinn.mybatis.multi.tenant.plugin.annotation;

import java.lang.annotation.*;

/**
 * 忽略多租户字段注解
 * <p>
 * 作用在Mapper方法上
 * </p>
 *
 * @author wency_cai
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreTenantIdField {
}
