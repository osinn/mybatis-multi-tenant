package com.gitee.osinn.mybatis.multi.tenant.plugin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gitee.osinn.mybatis.multi.tenant.plugin.mapper")
public class TenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantApplication.class, args);
    }
//
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
////        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
////            @Override
////            public Expression getTenantId() {
////                return new LongValue(1);
////            }
////
////            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
////            @Override
////            public boolean ignoreTable(String tableName) {
////                return false;
////            }
////        }));
//        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
//        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
////        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
//
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
//
//        return interceptor;
//    }
}

