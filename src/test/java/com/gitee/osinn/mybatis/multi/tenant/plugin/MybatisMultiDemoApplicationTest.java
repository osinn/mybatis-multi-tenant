package com.gitee.osinn.mybatis.multi.tenant.plugin;

import com.gitee.osinn.mybatis.multi.tenant.plugin.entity.UserEntity;
import com.gitee.osinn.mybatis.multi.tenant.plugin.mapper.UserMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenantApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MybatisMultiDemoApplicationTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        userMapper.selectTestWhere(1L);
    }

    @Test
    public void fetchUserAndRole() {
        List<UserEntity> userEntities = userMapper.fetchUserAndRole();
        System.out.println(userEntities.toString());
    }

    @Test
    public void deleteByUserId() {
        userMapper.deleteByUserId(10L);
    }

    @Test
    public void deleteById() {
        userMapper.deleteById(10L);
    }

    @Test
    public void fetchUserByRoleId() {
        userMapper.fetchUserByRoleId(1L);
    }

    @Test
    public void testUnionSelect() {
        List<UserEntity> userEntities = userMapper.testUnionSelect();
        System.out.println(userEntities.toString());
    }

    @Test
    public void checkUserName() {
        boolean root = userMapper.checkUserName("root");
        System.out.println(root);
    }
    @Test
    public void batchSave() {
        List<UserEntity> userList = Lists.newArrayList();
        userList.add(new UserEntity(11L, "root1", "123",1));
        userList.add(new UserEntity(12L, "root2", "123",1));
        userList.add(new UserEntity(13L, "root3", "123",1));
        userMapper.batchSave(userList);
    }

    @Test
    public void save() {
        userMapper.save(new UserEntity(13L, "root3", "123",1));
    }

//    @Test
//    public void testMybatisPlusPage() {
//        Page page = new Page(1,10);
//        List<UserEntity> userEntities = userMapper.testMybatisPlusPage(page);
//        System.out.println(userEntities.toString());
//    }


//
//    @Test
//    public void pagehelper() {
//        PageHelper.startPage(2, 2);
//        List<UserEntity> userEntities = userMapper.fetchUserAndRole();
//        PageInfo<UserEntity> pageInfoUserList = new PageInfo<>(userEntities);
//        System.out.println(pageInfoUserList.toString());
//    }

    @Test
    public void insertBook() {
        userMapper.deleteById(1L);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        userEntity.setUsername("测试多租户");
        userEntity.setPassword("123456");
        userMapper.save(userEntity);
        userEntity = userMapper.selectTestWhere(1L);
        System.out.println("根据书本名称查询：" + userEntity.toString());
        userEntity.setUsername("测试多租户更新");
        userMapper.updateById(userEntity);
        userEntity = userMapper.selectTestWhere(1L);
        System.out.println("根据书本名ID查询：" + userEntity.toString());

        List<UserEntity> userEntityList = userMapper.selectAll();
        System.out.println("查询全部书本：" + userEntityList.toString());
    }


}
