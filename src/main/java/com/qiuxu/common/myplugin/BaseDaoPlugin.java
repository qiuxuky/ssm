package com.qiuxu.common.myplugin;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

public class BaseDaoPlugin extends PluginAdapter {

	private Set<String> mappers = new HashSet<String>();
	private boolean caseSensitive = false;
	// 开始的分隔符，例如mysql为`，sqlserver为[
	private String beginningDelimiter = "";
	// 结束的分隔符，例如mysql为`，sqlserver为]
	private String endingDelimiter = "";
	// 数据库模式
	private String schema;
	// 注释生成器
	private CommentGeneratorConfiguration commentCfg;

	// 强制生成注解
	private boolean forceAnnotation;

	public String getDelimiterName(String name) {
		StringBuilder nameBuilder = new StringBuilder();
		if (StringUtility.stringHasValue(schema)) {
			nameBuilder.append(schema);
			nameBuilder.append(".");
		}
		nameBuilder.append(beginningDelimiter);
		nameBuilder.append(name);
		nameBuilder.append(endingDelimiter);
		return nameBuilder.toString();
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	/**
	 * 生成基础实体类
	 *
	 * @param topLevelClass
	 * @param introspectedTable
	 * @return
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		processEntityClass(topLevelClass, introspectedTable);
		return true;
	}

	/**
	 * 生成实体类注解KEY对象
	 *
	 * @param topLevelClass
	 * @param introspectedTable
	 * @return
	 */
	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		processEntityClass(topLevelClass, introspectedTable);
		return true;
	}

	/**
	 * 处理实体类的包和@Table注解
	 *
	 * @param topLevelClass
	 * @param introspectedTable
	 */
	private void processEntityClass(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		// 添加序列化版本
		makeSerializable(topLevelClass, introspectedTable);
		// 引入JPA注解
		topLevelClass.addImportedType("javax.persistence.*");
		String tableName = introspectedTable
				.getFullyQualifiedTableNameAtRuntime();
		// 如果包含空格，或者需要分隔符，需要完善
		if (StringUtility.stringContainsSpace(tableName)) {
			tableName = context.getBeginningDelimiter() + tableName
					+ context.getEndingDelimiter();
		}
		// 是否忽略大小写，对于区分大小写的数据库，会有用
		if (caseSensitive
				&& !topLevelClass.getType().getShortName().equals(tableName)) {
			topLevelClass.addAnnotation("@Table(name = \""
					+ getDelimiterName(tableName) + "\")");
		} else if (!topLevelClass.getType().getShortName()
				.equalsIgnoreCase(tableName)) {
			topLevelClass.addAnnotation("@Table(name = \""
					+ getDelimiterName(tableName) + "\")");
		} else if (StringUtility.stringHasValue(schema)
				|| StringUtility.stringHasValue(beginningDelimiter)
				|| StringUtility.stringHasValue(endingDelimiter)) {
			topLevelClass.addAnnotation("@Table(name = \""
					+ getDelimiterName(tableName) + "\")");
		} else if (forceAnnotation) {
			topLevelClass.addAnnotation("@Table(name = \""
					+ getDelimiterName(tableName) + "\")");
		}
	}

	/**
	 * 生成带BLOB字段的对象
	 *
	 * @param topLevelClass
	 * @param introspectedTable
	 * @return
	 */
	@Override
	public boolean modelRecordWithBLOBsClassGenerated(
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		processEntityClass(topLevelClass, introspectedTable);
		return false;
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
		// 设置默认的注释生成器
		commentCfg = new CommentGeneratorConfiguration();
		commentCfg.setConfigurationType(MyCommentGenerator.class
				.getCanonicalName());
		context.setCommentGeneratorConfiguration(commentCfg);
		// 支持oracle获取注释#114
		context.getJdbcConnectionConfiguration().addProperty(
				"remarksReporting", "true");
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		String mappers = this.properties.getProperty("mappers");
		for (String mapper : mappers.split(",")) {
			this.mappers.add(mapper);
		}
	}

	/**
	 * 生成的Dao接口
	 */
	@Override
	public boolean clientGenerated(Interface interfaze,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// dao接口添加自定义注解方便扫描
		FullyQualifiedJavaType daoType = new FullyQualifiedJavaType(
				"com.qiuxu.common.annotation.MyBatisDao");
		interfaze.addImportedType(daoType);
		interfaze.addAnnotation("@MyBatisDao");
		// 获取实体类
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(
				introspectedTable.getBaseRecordType());
		// import接口
		for (String mapper : mappers) {
			interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
			interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<"
					+ entityType.getShortName() + ">"));
		}
		// import实体类
		interfaze.addImportedType(entityType);

		return super.clientGenerated(interfaze, topLevelClass,introspectedTable);
	}

	/**
	 * 拼装SQL语句生成Mapper接口映射文件
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document,
			IntrospectedTable introspectedTable) {
		
		XmlElement rootElement = document.getRootElement();
		// 数据库表名
		String tableName = introspectedTable
				.getFullyQualifiedTableNameAtRuntime();
		// 主键
		IntrospectedColumn pkColumn = introspectedTable.getPrimaryKeyColumns()
				.get(0);
		// 公共字段
		XmlElement columnSql = new XmlElement("sql");
		columnSql.addAttribute(new Attribute("id", "sql_columns"));
		StringBuilder columnStr = new StringBuilder();

		// 添加公共where
		XmlElement whereSql = new XmlElement("sql");
		whereSql.addAttribute(new Attribute("id", "sql_where"));
		XmlElement where = new XmlElement("where");
		StringBuilder whereStr = new StringBuilder();

		// 拼装更新字段
		XmlElement updateSql = new XmlElement("sql");
		updateSql.addAttribute(new Attribute("id", "sql_update"));

		// 新增数据
		XmlElement save = new XmlElement("insert");
		save.addAttribute(new Attribute("id", "save"));
		save.addAttribute(new Attribute("keyProperty", pkColumn
				.getJavaProperty()));
		save.addAttribute(new Attribute("useGeneratedKeys", "true"));
		StringBuilder saveStr = new StringBuilder("insert into ").append(
				tableName).append("(");
		// 要插入的字段(排除自增主键)
		StringBuilder saveColumn = new StringBuilder();
		// 要保存的值
		StringBuilder saveValue = new StringBuilder();

		// 批量保存
		XmlElement batchSave = new XmlElement("insert");
		batchSave.addAttribute(new Attribute("id", "batchSave"));
		StringBuilder btcSaveStr = new StringBuilder("insert into ").append(
				tableName).append("(");

		// 更新数据
		XmlElement update = new XmlElement("update");
		update.addAttribute(new Attribute("id", "update"));
		StringBuilder updateStr = new StringBuilder("update ")
				.append(tableName).append(" set ")
				.append(pkColumn.getActualColumnName()).append(" = #{")
				.append(pkColumn.getJavaProperty()).append("}");
		update.addElement(new TextElement(updateStr.toString()));

		// 批量更新
		XmlElement batchUpdate = new XmlElement("update");
		batchUpdate.addAttribute(new Attribute("id", "batchUpdate"));
		XmlElement foreachUpdate = new XmlElement("foreach");
		foreachUpdate.addAttribute(new Attribute("collection", "list"));
		foreachUpdate.addAttribute(new Attribute("item", "item"));
		foreachUpdate.addAttribute(new Attribute("index", "index"));
		foreachUpdate.addAttribute(new Attribute("open", ""));
		foreachUpdate.addAttribute(new Attribute("close", ""));
		foreachUpdate.addAttribute(new Attribute("separator", ";"));
		batchUpdate.addElement(foreachUpdate);
		StringBuilder btcUpdateStr = new StringBuilder("update ")
				.append(tableName).append(" set ")
				.append(pkColumn.getActualColumnName()).append(" = #{item.")
				.append(pkColumn.getJavaProperty()).append("}");
		;
		foreachUpdate.addElement(new TextElement(btcUpdateStr.toString()));

		// 数据库字段名
		String columnName = null;
		// java字段名
		String javaProperty = null;
		for (IntrospectedColumn introspectedColumn : introspectedTable
				.getAllColumns()) {
			columnName = MyBatis3FormattingUtilities
					.getEscapedColumnName(introspectedColumn);
			javaProperty = introspectedColumn.getJavaProperty();
			// 拼装字段
			columnStr.append(columnName).append(",");

			// 拼装公共条件
			XmlElement isNotNullElement = new XmlElement("if");
			whereStr.setLength(0);
			whereStr.append("null != item.").append(javaProperty)
					.append(" and '' != item.").append(javaProperty);
			isNotNullElement.addAttribute(new Attribute("test", whereStr
					.toString()));

			whereStr.setLength(0);
			whereStr.append(" , ").append(columnName).append(" = #{item.")
					.append(javaProperty).append("}");
			isNotNullElement.addElement(new TextElement(whereStr.toString()));

			where.addElement(isNotNullElement);
			updateSql.addElement(isNotNullElement);

			// 保存SQL
			if (!introspectedColumn.isAutoIncrement()) {
				saveColumn.append(",").append(columnName);
				if (Types.TIMESTAMP == introspectedColumn.getJdbcType()) {
					saveValue.append(", now()");
				} else {
					saveValue.append(", #{item.").append(javaProperty)
							.append("}");
				}
			}

		}
		String columns = columnStr.substring(0, columnStr.length() - 1);

		columnStr = new StringBuilder("select ").append(columns)
				.append(" from ").append(tableName);
		columnSql.addElement(new TextElement(columns));
		rootElement.addElement(columnSql);

		whereSql.addElement(new TextElement(where.getFormattedContent(0)
				.replaceAll(",", "and")));
		rootElement.addElement(whereSql);

		rootElement.addElement(updateSql);

		saveStr.append(saveColumn.substring(1)).append(") values(")
				.append(saveValue.substring(1)).append(")");
		save.addElement(new TextElement(saveStr.toString()));

		btcSaveStr.append(saveColumn.substring(1)).append(") values");
		batchSave.addElement(new TextElement(btcSaveStr.toString()));
		btcSaveStr.setLength(0);
		XmlElement foreach = new XmlElement("foreach");
		foreach.addAttribute(new Attribute("collection", "list"));
		foreach.addAttribute(new Attribute("item", "item"));
		foreach.addAttribute(new Attribute("index", "index"));
		foreach.addAttribute(new Attribute("separator", ","));
		foreach.addElement(new TextElement("("
				+ saveValue.toString().substring(1) + ")"));
		batchSave.addElement(foreach);

		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_update"));
		columnStr = new StringBuilder(" where ")
				.append(pkColumn.getActualColumnName()).append(" = #{")
				.append(pkColumn.getJavaProperty()).append("}");

		update.addElement(include);
		update.addElement(new TextElement(columnStr.toString()));

		foreachUpdate.addElement(include);
		foreachUpdate.addElement(new TextElement(columnStr.toString()));
		rootElement.addElement(selectById(pkColumn, tableName));
		rootElement.addElement(selectXml("selectOne", tableName));
		rootElement.addElement(selectXml("selectList", tableName));
		rootElement.addElement(selectPage(tableName));
		rootElement.addElement(selectCount("count",tableName));
		rootElement.addElement(save);
		rootElement.addElement(batchSave);
		rootElement.addElement(update);
		rootElement.addElement(batchUpdate);
		rootElement.addElement(btcDels(tableName, pkColumn, "delArray", "array"));
		rootElement.addElement(btcDels(tableName, pkColumn, "delList", "list"));
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	private Element selectCount(String id, String tableName) {
		XmlElement select = new XmlElement("select");
		select.addAttribute(new Attribute("id", id));
		select.addAttribute(new Attribute("resultType", "int"));
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(*) from ").append(tableName);
		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_where"));
		select.addElement(new TextElement(countSql.toString()));
		select.addElement(include);
		return select;
	}

	private XmlElement select(String id, String tableName) {
		XmlElement select = new XmlElement("select");
		select.addAttribute(new Attribute("id", id));
		select.addAttribute(new Attribute("resultMap", "BaseResultMap"));

		select.addElement(new TextElement("select "));
		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_columns"));
		select.addElement(include);
		select.addElement(new TextElement(" from " + tableName));
		return select;
	}

	private XmlElement selectById(IntrospectedColumn pkColumn, String tableName) {
		XmlElement select = select("selectById", tableName);
		StringBuilder sb = new StringBuilder(" where ");
		sb.append(pkColumn.getActualColumnName());
		sb.append(" = ");
		sb.append(MyBatis3FormattingUtilities.getParameterClause(pkColumn));
		select.addElement(new TextElement(sb.toString()));
		return select;
	}

	private XmlElement selectPage(String tableName) {
		XmlElement select = selectXml("selectPage", tableName);
		select.addElement(new TextElement(
				" limit #{page.startRow}, #{page.pageSize}"));
		return select;
	}

	private XmlElement selectXml(String id, String tableName) {
		XmlElement select = select(id, tableName);
		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_where"));
		select.addElement(include);
		return select;
	}

	private XmlElement btcDels(String tableName, IntrospectedColumn pkColumn,
			String method, String type) {
		XmlElement delete = new XmlElement("delete");
		delete.addAttribute(new Attribute("id", method));
		delete.addElement(new TextElement("delete from " + tableName
				+ " where " + pkColumn.getActualColumnName() + " in"));
		XmlElement foreach = new XmlElement("foreach");
		foreach.addAttribute(new Attribute("collection", type));
		foreach.addAttribute(new Attribute("item", "item"));
		foreach.addAttribute(new Attribute("index", "index"));
		foreach.addAttribute(new Attribute("open", "("));
		foreach.addAttribute(new Attribute("separator", ","));
		foreach.addAttribute(new Attribute("close", ")"));
		foreach.addElement(new TextElement("#{item}"));
		delete.addElement(foreach);
		return delete;
	}

	// 序列化
	protected void makeSerializable(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		Field field = new Field();
		field.setFinal(true);
		field.setInitializationString("1L");
		field.setName("serialVersionUID");
		field.setStatic(true);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setVisibility(JavaVisibility.PRIVATE);
		context.getCommentGenerator().addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
	}

	// 以下设置为false,取消生成默认增删查改
	@Override
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertSelectiveMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(
			Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(
			Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
			Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method,
			Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertMethodGenerated(Method method,
			Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertSelectiveMethodGenerated(Method method,
			Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientSelectAllMethodGenerated(Method method,
			Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientSelectAllMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method,
			Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(
			Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(
			Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
			Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapSelectAllElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean providerGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean providerApplyWhereMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean providerInsertSelectiveMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(
			Method method, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		return false;
	}

}
