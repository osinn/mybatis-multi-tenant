package com.gitee.osinn.mybatis.multi.tenant.plugin.parser;

import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;


/**
 * sql解析器接口
 *
 * @author wency_cai
 */
public interface SqlParser {

    /**
     * sql语句处理入口
     *
     * @param sql 语句处理入口
     * @return 返回串改后的sql语句
     */
    String setTenantParameter(String sql);


    /**
     * @param selectBody select语句处理
     */
    void processSelectBody(SelectBody selectBody);

    /**
     * @param insert 语句处理
     */
    void processInsert(Insert insert);

    /**
     * @param update 语句处理
     */
    void processUpdate(Update update);

    /**
     * @param delete 删除语句处理
     */
    void processDelete(Delete delete);
}
