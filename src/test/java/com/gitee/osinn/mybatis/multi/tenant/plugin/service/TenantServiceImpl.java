package com.gitee.osinn.mybatis.multi.tenant.plugin.service;

import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 演示：提供多租户ID服务接口
 *
 * @author wency_cai
 */
@Service
public class TenantServiceImpl implements ITenantService<Integer>{

    @Override
    public List<Integer> getTenantIds() {
        // 查询系统多租户id
        int tenantId = 1;
        return Lists.newArrayList(tenantId);
    }
}
