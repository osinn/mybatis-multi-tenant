package com.gitee.osinn.mybatis.multi.tenant.plugin.mapper;

import com.gitee.osinn.mybatis.multi.tenant.plugin.annotation.IgnoreTenantIdField;
import com.gitee.osinn.mybatis.multi.tenant.plugin.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    UserEntity selectTestWhere(Long id);

    List<UserEntity> fetchUserAndRole();

    List<UserEntity> fetchUserByRoleId(Long roleId);

    boolean checkUserName(String userName);

    void deleteByUserId(Long id);

    void deleteById(Long id);

    List<UserEntity> testUnionSelect();

    void batchSave(@Param("userList") List<UserEntity> userList);

    void save(UserEntity userEntity);

    @IgnoreTenantIdField
    void deleteTestIgnoreTenantIdById(Long id);

    void updateById(UserEntity userEntity);

    List<UserEntity> selectAll();

//    List<UserEntity> testMybatisPlusPage(Page page);

}
