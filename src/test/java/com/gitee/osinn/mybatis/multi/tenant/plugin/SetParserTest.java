package com.gitee.osinn.mybatis.multi.tenant.plugin;

import com.gitee.osinn.mybatis.multi.tenant.plugin.handler.TenantInfoHandler;
import com.gitee.osinn.mybatis.multi.tenant.plugin.parser.DefaultSqlParser;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SetParserTest {
    DefaultSqlParser defaultSqlParser;

    @BeforeEach
    public void init() {
        defaultSqlParser = new DefaultSqlParser(new TenantInfoHandler() {

            @Override
            public List<Object> getTenantIds() {
                return Lists.newArrayList("2");
            }

            @Override
            public List<String> ignoreTablePrefix() {
                return Lists.newArrayList();
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }
        });
    }

    @Test
    public void test() {
//        String sql = "        SELECT" +
//                "            u.*," +
//                "            r.id as r_id," +
//                "            r.name as r_name" +
//                "        FROM" +
//                "            `user` u" +
//                "                LEFT JOIN user_role ur ON ur.user_id = u.id" +
//                "                LEFT JOIN role r ON r.id = ur.role_id and u.id = 22";
//        String sql = "SELECT id, name,tenant_id FROM role";

//        String sql = "select * from user s where s.name='333'";
//        String sql = "select u.*,g.name from user u join user_group g on u.groupId=g.groupId where u.name='123'";
//        String sql = "select tenant_id from people where id in (select id from user s)"; // in 子查询未加上租户字段
//        String sql = "update user u set ds=?, u.name=?,id='fdf' ,ddd=? where id =?";
//        String sql = "delete from user where id in ( select id from user s )";
//        String sql = "insert into user (id,name) values('0','ssss'),('1','dddd')";
//        String sql = "insert into user (id,name) select g.id,g.name from user_group g where id=1";
//

//        String sql = "SELECT EXISTS ( SELECT * FROM `user` WHERE username = ?) d";  // in 子查询未加上租户字段
//        String sql = "INSERT INTO hy_route_user (route_id,user_id,user_type,create_date) VALUES (1,2,3,4),(1,2,3,4),(1,2,3,4)";
//        String sql = "INSERT INTO `user` (`id`,`username`,`password`) VALUES (1, 'root', '123'),(2, 'user', '123', 2),(3, '张三', '123')";
//                String sql = "        SELECT\n" +
//                "            u.*,\n" +
//                "            r.id as r_id,\n" +
//                "            r.name as r_name\n" +
//                "        FROM\n" +
//                "            `user` u\n" +
//                "                LEFT JOIN user_role ur ON ur.user_id = u.id\n" +
//                "                RIGHT JOIN role r ON r.id = ur.role_id and u.id = 22";

//        String sql ="        SELECT\n" +
//                "            u.*,\n" +
//                "            r.id as r_id,\n" +
//                "            r.name as r_name\n" +
//                "        FROM\n" +
//                "            `user` u\n" +
//                "                LEFT JOIN user_role ur ON ur.user_id = u.id\n" +
//                "                LEFT JOIN role r ON r.id = ur.role_id";

//        String sql = "INSERT INTO `user` (`id`,`username`,`password`) VALUES (?,?,?) , (?,?,?) , (?,?,?)";
//        String sql = "SELECT COUNT( * ), id FROM `user` GROUP BY id HAVING COUNT( * ) >= 1";
        String sql = "SELECT * FROM `user` union SELECT id,account as username,`password`,tenant_id FROM account";
        String newSql = defaultSqlParser.setTenantParameter(sql);
        System.out.println(newSql);
    }
}
