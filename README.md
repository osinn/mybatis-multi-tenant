# 本项目已经停止维护
- 请使用 druid-multi-tenant-starter，地址https://github.com/osinn/druid-multi-tenant-starter

# mybatis-multi-tenant-spring-boot-starter
> mybatis 多租户插件，开箱即用，支持单个租户以及多个租户

# 支持第三方插件
- [x] Mybatis Plus
- [x] Mybatis PageHelper 分页插件

# 快速开始
- 在已经集成`Mybatis`项目中引入以下依赖

```
<dependency>
    <groupId>com.gitee.osinn</groupId>
    <artifactId>mybatis-multi-tenant-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### `application.yml`配置Mybatis 多租户参数
```
mybatis:
  tenant:
    config:
      # 是否启用多租户功能
      enable: true
      #  数据库表中租户ID的字段名
      tenant-id-column: tenant_id
      # 是否忽略表按租户ID过滤,默认所有表都按租户ID过滤
#      ignore-table:
#        - 'user_' # 表名或表前缀
```
### 实现提供获取多租户值接口
- 需要实现ITenantService接口提供获取多租户ID值

```
/**
 * 演示：提供多租户ID服务接口
 *
 * @author wency_cai
 */
@Service
public class TenantServiceImpl implements ITenantService<Integer>{

    @Override
    public List<Integer> getTenantIds() {
        // 查询系统多租户id,如果有多个返回多个值即可
        int tenantId = 1;
        return Lists.newArrayList(tenantId);
    }
}
```
- 到此整合完成

### 多租户忽略Mapper方法
```
public interface UserMapper {

    /**
     * 添加@IgnoreTenantIdField注解来忽略设置多租户字段
     */
    @IgnoreTenantIdField
    void deleteTestIgnoreTenantIdById(Long id);
}
```

# 演示
#### 无条件查询输入

```sql
SELECT id, name,tenant_id FROM role
```

#### 输出

```sql
SELECT id, name,tenant_id FROM role WHERE tenant_id = '2'
```

#### where条件查询输入

```sql
select * from user s where s.name='333'
```

#### 输出

```sql
SELECT * FROM user s WHERE s.tenant_id = '2' AND s.name = '333'
```

#### IN条件查询输入

```sql
select tenant_id from people where id in (select id from user s)
```

#### 输出

```sql
SELECT
	tenant_id 
FROM
	people 
WHERE
	tenant_id = '2' 
	AND id IN ( SELECT id FROM USER s WHERE s.tenant_id = '2' )
```

#### where更新操作输入

```sql
update user u set ds=?, u.name=?,id='fdf' ,ddd=? where id =?
```

#### 输出

```sql
UPDATE user u SET ds = ?, u.name = ?, id = 'fdf', ddd = ? 
WHERE u.tenant_id = '2' AND id = ?
```

#### IN条件删除输入

```sql
delete from user where id in ( select id from user s )
```

#### 输出

```sql
DELETE FROM user WHERE tenant_id = '2' 
   AND id IN (SELECT id FROM user s WHERE s.tenant_id = '2')
```

#### EXISTS查询输入

```sql
SELECT EXISTS ( SELECT * FROM `user` WHERE username = ?) d
```

#### 输出

```sql
SELECT EXISTS (SELECT * FROM `user` WHERE tenant_id = '2' AND username = ?) d
```

#### 批量保存输入

```sql
INSERT INTO `user` (`id`,`username`,`password`) 
VALUES (?,?,?) , (?,?,?) , (?,?,?)
```

#### 输出

```sql
INSERT INTO `user` (`id`, `username`, `password`, tenant_id) 
VALUES (?, ?, ?, 2), (?, ?, ?, 2), (?, ?, ?, 2)
```



#### 多表关联查询输入

```sql
SELECT
	u.*,
	r.id AS r_id,
	r.NAME AS r_name 
FROM
	`user` u
	LEFT JOIN user_role ur ON ur.user_id = u.id
	LEFT JOIN role r ON r.id = ur.role_id 
	AND u.id = 22
```

#### 输出

```sql
SELECT
	u.*,
	r.id AS r_id,
	r.NAME AS r_name 
FROM
	`user` u
	LEFT JOIN user_role ur ON ur.tenant_id = '2' 
	AND ur.user_id = u.id
	LEFT JOIN role r ON r.tenant_id = '2' 
	AND r.id = ur.role_id 
	AND u.id = 22 
WHERE
	u.tenant_id = '2'
```

#### 分组条件查询输入

```sql
SELECT COUNT( * ), id FROM `user` GROUP BY id HAVING COUNT( * ) >= 1
```

#### 输出

```sql
SELECT COUNT(*), id FROM `user` 
WHERE tenant_id = '2' GROUP BY id HAVING COUNT(*) >= 1
```
#### union查询输入
```sql
SELECT * FROM `user` 
union 
SELECT id,account as username,`password`,tenant_id FROM account
```
#### 输出
```sql
SELECT
    *
FROM `user` WHERE tenant_id = 1
union
SELECT
    id,
    account AS username,
    `password`,
    tenant_id
FROM account WHERE tenant_id = 1
```
