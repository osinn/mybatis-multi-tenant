package com.gitee.osinn.mybatis.multi.tenant.plugin.parser;

import com.gitee.osinn.mybatis.multi.tenant.plugin.filter.MultiTenantFilter;
import com.gitee.osinn.mybatis.multi.tenant.plugin.filter.MybatisMultiTenantFilter;
import com.gitee.osinn.mybatis.multi.tenant.plugin.handler.TenantInfoHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * sql解析器实现
 *
 * @author wency_cai
 */
public class DefaultSqlParser implements SqlParser {


    private static final Logger logger = LoggerFactory.getLogger(DefaultSqlParser.class);
    /**
     * 多租户处理接口
     */
    private TenantInfoHandler tenantInfoHandler;

    private MultiTenantFilter multiTenantFilter;

    public DefaultSqlParser(TenantInfoHandler tenantInfoHandler) {
        this.tenantInfoHandler = tenantInfoHandler;
        multiTenantFilter = new MybatisMultiTenantFilter(tenantInfoHandler.ignoreTablePrefix());
    }

    private boolean doTableFilter(String table) {
        return multiTenantFilter.doTableFilter(table);
    }

    @Override
    public String setTenantParameter(String sql) {
        logger.debug("old sql:{}", sql);
        Statement stmt = null;
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            logger.debug("解析：" + e.getMessage(), e);
            logger.error("解析sql[{}] error:{}", sql, e.getMessage());
            return sql;
        }
        if (stmt instanceof Insert) {
            processInsert((Insert) stmt);
        }
        if (stmt instanceof Select) {
            Select select = (Select) stmt;
            processSelectBody(select.getSelectBody());
        }
        if (stmt instanceof Update) {
            processUpdate((Update) stmt);
        }
        if (stmt instanceof Delete) {
            processDelete((Delete) stmt);
        }
        logger.debug("new sql:{}", stmt);
        return stmt.toString();
    }


    /**
     * 处理SelectBody
     */
    @Override
    public void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSubSelect() != null) {
                processSelectBody(withItem.getSubSelect().getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
        }
    }

    @Override
    public void processInsert(Insert insert) {
        if (doTableFilter(insert.getTable().getName())) {
            List<Column> columns = insert.getColumns();
            if (columns == null || columns.isEmpty()) {
                // 针对不给列名的insert 不处理
                return;
            }
            String tenantIdColumn = this.tenantInfoHandler.getTenantIdColumn();
            if (ignoreInsert(columns, tenantIdColumn)) {
                // 针对已给出租户列的insert 不处理
                return;
            }

            insert.getColumns().add(new Column(this.tenantInfoHandler.getTenantIdColumn()));
            if (insert.getSelect() != null) {
                processPlainSelect((PlainSelect) insert.getSelect().getSelectBody(), true);
            } else if (insert.getItemsList() != null) {
                ItemsList itemsList = insert.getItemsList();
                if (itemsList instanceof MultiExpressionList) {
                    ((MultiExpressionList) itemsList).getExpressionLists().forEach(el -> el.getExpressions().add(getValue(this.getTenantId())));
                } else {
                    ((ExpressionList) itemsList).getExpressions().add(getValue(this.getTenantId()));
                }
            } else {
                throw new RuntimeException("无法处理的 sql");
            }
        }
    }

    /**
     * @param update 语句处理
     */
    @Override
    public void processUpdate(Update update) {
        //获得where条件表达式
        Expression where = update.getWhere();
        processWhereSubSelect(where);
        update.setWhere(builderExpression(where, update.getTable()));
    }

    /**
     * @param delete 删除语句处理
     */
    @Override
    public void processDelete(Delete delete) {
        //获得where条件表达式
        Expression where = delete.getWhere();
        processWhereSubSelect(where);
        delete.setWhere(builderExpression(where, delete.getTable()));
    }

    private Expression getValue(Object val) {
        if (val instanceof Number) {
            return new LongValue(val.toString());
        } else {
            return new StringValue("'" + val + "'");
        }
    }

    private Expression getTenantExpression(Table table) {
        Expression tenantExpression = null;
        List<Object> tenantIds = this.tenantInfoHandler.getTenantIds();
        //生成字段名
        Column tenantColumn = getAliasColumn(table);
        if (tenantIds.size() == 1) {
            EqualsTo equalsTo = new EqualsTo();
            tenantExpression = equalsTo;
            equalsTo.setLeftExpression(tenantColumn);
            equalsTo.setRightExpression(getValue(tenantIds.get(0)));
        } else {
            //多租户身份
            InExpression inExpression = new InExpression();
            tenantExpression = inExpression;
            inExpression.setLeftExpression(tenantColumn);
            List<Expression> valueList = new ArrayList<>();
            for (Object tid : tenantIds) {
                valueList.add(getValue(tid));
            }
            inExpression.setRightItemsList(new ExpressionList(valueList));
        }
        return tenantExpression;
    }

    /**
     * 租户字段别名设置
     * <p>tenantId 或 tableAlias.tenantId</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(".");
        }
        column.append(this.tenantInfoHandler.getTenantIdColumn());
        return new Column(column.toString());
    }

    /**
     * @param join 处理联接语句
     */
    public void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (doTableFilter(fromTable.getName())) {
                // 联接语句 on 表达式肯定只有一个
                Collection<Expression> originOnExpressions = join.getOnExpressions();
                List<Expression> onExpressions = new LinkedList<>();
                onExpressions.add(builderExpression(originOnExpressions.iterator().next(), fromTable));
                join.setOnExpressions(onExpressions);
            }

        }
    }


    /**
     * 处理 joins
     *
     * @param joins join 集合
     */
    private void processJoins(List<Join> joins) {
        //对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
        Deque<Table> tables = new LinkedList<>();
        for (Join join : joins) {
            // 处理 on 表达式
            FromItem fromItem = join.getRightItem();
            if (fromItem instanceof Table) {
                Table fromTable = (Table) fromItem;
                // 获取 join 尾缀的 on 表达式列表
                Collection<Expression> originOnExpressions = join.getOnExpressions();
                // 正常 join on 表达式只有一个，立刻处理
                if (originOnExpressions.size() == 1) {
                    processJoin(join);
                    continue;
                }
                // 当前表是否忽略
                boolean needIgnore = doTableFilter(fromTable.getName());
                // 表名压栈，忽略的表压入 null，以便后续不处理
                tables.push(needIgnore ? null : fromTable);
                // 尾缀多个 on 表达式的时候统一处理
                if (originOnExpressions.size() > 1) {
                    Collection<Expression> onExpressions = new LinkedList<>();
                    for (Expression originOnExpression : originOnExpressions) {
                        Table currentTable = tables.poll();
                        if (currentTable == null) {
                            onExpressions.add(originOnExpression);
                        } else {
                            onExpressions.add(builderExpression(originOnExpression, currentTable));
                        }
                    }
                    join.setOnExpressions(onExpressions);
                }
            } else {
                // 处理右边连接的子表达式
                processFromItem(fromItem);
            }
        }
    }

    /**
     * @param expression 处理条件
     * @param table      表名称
     * @return 返回处理后的条件
     */
    public Expression builderExpression(Expression expression, Table table) {
        Expression tenantExpression = getTenantExpression(table);
        //加入判断防止条件为空时生成 "and null" 导致查询结果为空
        if (expression == null) {
            return tenantExpression;
        } else {
            if (expression instanceof BinaryExpression) {
                BinaryExpression binaryExpression = (BinaryExpression) expression;
                if (binaryExpression.getLeftExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getLeftExpression());
                }
                if (binaryExpression.getRightExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getRightExpression());
                }
            }
            if (expression instanceof OrExpression) {
                return new AndExpression(new Parenthesis(tenantExpression), expression);
            } else {
                return new AndExpression(tenantExpression, expression);
            }
        }
    }

    /**
     * @param plainSelect 处理PlainSelect
     */
    public void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * @param plainSelect 处理PlainSelect
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */
    public void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        Expression where = plainSelect.getWhere();
        processWhereSubSelect(where);
        /*
          SELECT EXISTS (SELECT * FROM user WHERE is_delete=0 ) d
          此时 fromItem 等于 null
         */
        if (fromItem == null) {
            plainSelect.getSelectItems().forEach(selectItem -> {
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                processWhereSubSelect(selectExpressionItem.getExpression());
            });
        } else if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (doTableFilter(fromTable.getName())) {
                plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
                if (addColumn) {
                    plainSelect.getSelectItems().add(new SelectExpressionItem(new Column("'" + this.getTenantId() + "'")));
                }
            }
        } else {
            processFromItem(fromItem);
        }

        //#3087 github
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (!(selectItems == null || selectItems.isEmpty())) {
            selectItems.forEach(this::processSelectItem);
        }

        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            processJoins(joins);
        }
    }

    protected void processSelectItem(SelectItem selectItem) {
        if (selectItem instanceof SelectExpressionItem) {
            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
            if (selectExpressionItem.getExpression() instanceof SubSelect) {
                processSelectBody(((SubSelect) selectExpressionItem.getExpression()).getSelectBody());
            } else if (selectExpressionItem.getExpression() instanceof Function) {
                processFunction((Function) selectExpressionItem.getExpression());
            }
        }
    }


    /**
     * 处理函数
     * <p>支持: 1. select fun(args..) 2. select fun1(fun2(args..),args..)</p>
     * <p> fixed gitee pulls/141</p>
     *
     * @param function 函数
     */
    protected void processFunction(Function function) {
        ExpressionList parameters = function.getParameters();
        if (parameters != null) {
            parameters.getExpressions().forEach(expression -> {
                if (expression instanceof SubSelect) {
                    processSelectBody(((SubSelect) expression).getSelectBody());
                } else if (expression instanceof Function) {
                    processFunction((Function) expression);
                }
            });
        }
    }

    /**
     * @param fromItem 处理子查询等
     */
    public void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null) {
                processJoins(subJoin.getJoinList());
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {

        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }


    /**
     * 处理where条件内的子查询
     * <p>
     * 支持如下:
     * 1. in
     * 2. =
     * 3. >
     * 4. <
     * 5. >=
     * 6. <=
     * 7. <>
     * 8. EXISTS
     * 9. NOT EXISTS
     * </p>
     * 前提条件:
     * 1. 子查询必须放在小括号中
     * 2. 子查询一般放在比较操作符的右边
     *
     * @param where where 条件
     */
    protected void processWhereSubSelect(Expression where) {
        if (where == null) {
            return;
        }
        if (where instanceof FromItem) {
            processFromItem((FromItem) where);
            return;
        }
        if (where.toString().indexOf("SELECT") > 0) {
            // 有子查询
            if (where instanceof BinaryExpression) {
                // 比较符号 , and , or , 等等
                BinaryExpression expression = (BinaryExpression) where;
                processWhereSubSelect(expression.getLeftExpression());
                processWhereSubSelect(expression.getRightExpression());
            } else if (where instanceof InExpression) {
                // in
                InExpression expression = (InExpression) where;
                ItemsList itemsList = expression.getRightItemsList();
                if (itemsList instanceof SubSelect) {
                    processSelectBody(((SubSelect) itemsList).getSelectBody());
                }
            } else if (where instanceof ExistsExpression) {
                // exists
                ExistsExpression expression = (ExistsExpression) where;
                processWhereSubSelect(expression.getRightExpression());
            } else if (where instanceof NotExpression) {
                // not exists
                NotExpression expression = (NotExpression) where;
                processWhereSubSelect(expression.getExpression());
            } else if (where instanceof Parenthesis) {
                Parenthesis expression = (Parenthesis) where;
                processWhereSubSelect(expression.getExpression());
            }
        }
    }

    /**
     * 忽略插入租户字段逻辑
     *
     * @param columns        插入字段
     * @param tenantIdColumn 租户 ID 字段
     * @return 返回true 表示忽略
     */
    private boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return columns.stream().map(Column::getColumnName).anyMatch(i -> i.equalsIgnoreCase(tenantIdColumn));
    }

    private Object getTenantId() {
        if (tenantInfoHandler.getTenantIds().isEmpty()) {
            return null;
        } else {
            return tenantInfoHandler.getTenantIds().get(0);
        }
    }
}
