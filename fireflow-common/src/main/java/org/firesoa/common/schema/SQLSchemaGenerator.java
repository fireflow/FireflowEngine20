package org.firesoa.common.schema;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.constants.Constants;
import org.gibello.zql.ParseException;
import org.gibello.zql.ZConstant;
import org.gibello.zql.ZDelete;
import org.gibello.zql.ZExp;
import org.gibello.zql.ZExpression;
import org.gibello.zql.ZInsert;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZUpdate;
import org.gibello.zql.ZqlParser;

public class SQLSchemaGenerator {
	public static final String DATA_SET_ELEMENT = "DataSet";
	public static final String ROW_ELEMENT = "Row";
	public static final String WHERE_ELEMENT = "Where";
	public static final String SET_ELEMENT = "Set";
	public static final String UPDATE_ELEMENT = "Update";
	public static final String VALUES_ELEMENT = "Values";

	public static ZStatement parseSQL(String sqlArg) throws ParseException {
		String sql = sqlArg;
		if (!sql.endsWith(";")) {
			sql = sql + ";";
		}
		if (StringUtils.isEmpty(sql))
			return null;
		ByteArrayInputStream in = null;
		try {
			in = new ByteArrayInputStream(sql.getBytes(Charset.defaultCharset().name()));
		} catch (Exception e) {

		}
		if (in == null)
			return null;
		ZqlParser zqlGenerator = new ZqlParser(in);

		try {
			ZStatement zstatement = zqlGenerator.readStatement();// Sql语句在设置到DBQuery时，经过了校验
			return zstatement;
		} catch (ParseException e) {
			throw e;
		}
	}

	public static XmlSchemaCollection generateXmlSchemaCollectionForSQL(
			String sql, String targetNsUri, Connection conn)
			throws ParseException, SQLException {
		ZStatement zstatement = parseSQL(sql);
		if (zstatement != null && (zstatement instanceof ZQuery)) {
			return generateQuerySchema((ZQuery) zstatement, sql, targetNsUri,
					conn);
		} else if (zstatement != null && (zstatement instanceof ZDelete)) {
			return generateDeleteSchema((ZDelete) zstatement, sql, targetNsUri,
					conn);
		} else if (zstatement != null && (zstatement instanceof ZUpdate)) {
			return generateUpdateSchema((ZUpdate) zstatement, sql, targetNsUri,
					conn);
		}else if (zstatement != null && (zstatement instanceof ZInsert)){
			return generateInsertSchema((ZInsert) zstatement, sql, targetNsUri,
					conn);
		}
		return null;
	}

	protected static XmlSchemaCollection generateUpdateSchema(ZUpdate zu,
			String sql, String targetNsUri, Connection conn)
			throws ParseException, SQLException {
		ParameterMetaData parameterMetaData = null;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		parameterMetaData = pstmt.getParameterMetaData();

		// //////////////////////////////////////////////////////////
		// /////////////////// 构造xsd schema ///////////////////
		// //////////////////////////////////////////////////////////
		XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
		XmlSchema xmlschema = new XmlSchema(targetNsUri, xmlSchemaCollection);
		xmlschema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
		xmlschema.setAttributeFormDefault(XmlSchemaForm.UNQUALIFIED);
		// /////////////////1、构造set 的schema //////////////////////
		int count = zu.getColumnUpdateCount();
		Hashtable set = zu.getSet();

		int parameterIndex = 0;

		XmlSchemaSequence setFieldsTypeSequence = new XmlSchemaSequence();
		for (int index = 1; index <= count; index++) {
			String name = zu.getColumnUpdateName(index);
			ZExp e = (ZExp) set.get(name);
			if (isPreparedColumn(e)) {
				parameterIndex++;
				int sqlType = parameterMetaData
						.getParameterType(parameterIndex);
				QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP.get(sqlType);

				XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
						false);
				fieldElement.setName(name);
				fieldElement.setSchemaTypeName(type != null ? type
						: Constants.XSD_STRING);
				setFieldsTypeSequence.getItems().add(fieldElement);
			}
		}
		XmlSchemaComplexType setFieldsType = new XmlSchemaComplexType(
				xmlschema, true);
		setFieldsType.setName(SET_ELEMENT + "Type");
		setFieldsType.setParticle(setFieldsTypeSequence);

		// /////////////// 2、构造update条件的schema /////////////////
		ZExpression w = (ZExpression) zu.getWhere();
		List<String> whereColumns = new ArrayList<String>();
		if (w != null) {
			handleWhere(w, whereColumns);
		}

		XmlSchemaSequence whereFieldsTypeSequence = new XmlSchemaSequence();
		for (int i = 0; i < whereColumns.size(); i++) {
			String preparedColumn = whereColumns.get(i);
			XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
					false);
			fieldElement.setName(preparedColumn);
			QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP
					.get(parameterMetaData.getParameterType(i + 1
							+ parameterIndex));
			fieldElement.setSchemaTypeName(type != null ? type
					: Constants.XSD_STRING);
			whereFieldsTypeSequence.getItems().add(fieldElement);
		}

		XmlSchemaComplexType whereFieldsType = new XmlSchemaComplexType(
				xmlschema, true);
		whereFieldsType.setName(WHERE_ELEMENT + "Type");
		whereFieldsType.setParticle(whereFieldsTypeSequence);

		// /////////////// 3、构造整个update的schema /////////////////
		XmlSchemaSequence updateTypeSequence = new XmlSchemaSequence();
		XmlSchemaElement setFieldsElement = new XmlSchemaElement(xmlschema,
				false);
		setFieldsElement.setName(SET_ELEMENT);
		setFieldsElement.setSchemaTypeName(setFieldsType.getQName());
		updateTypeSequence.getItems().add(setFieldsElement);

		XmlSchemaElement whereFieldsElement = new XmlSchemaElement(xmlschema,
				false);
		whereFieldsElement.setName(WHERE_ELEMENT);
		whereFieldsElement.setSchemaTypeName(whereFieldsType.getQName());
		updateTypeSequence.getItems().add(whereFieldsElement);

		XmlSchemaComplexType updateType = new XmlSchemaComplexType(xmlschema,
				true);
		updateType.setName(UPDATE_ELEMENT + "Type");
		updateType.setParticle(updateTypeSequence);

		XmlSchemaElement updateElement = new XmlSchemaElement(xmlschema, true);
		updateElement.setName(UPDATE_ELEMENT);
		updateElement.setSchemaTypeName(updateType.getQName());

		return xmlSchemaCollection;
	}

	protected static XmlSchemaCollection generateInsertSchema(ZInsert zi,
			String sql, String targetNsUri, Connection conn)
			throws ParseException, SQLException {
		ParameterMetaData parameterMetaData = null;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		parameterMetaData = pstmt.getParameterMetaData();

		// //////////////////////////////////////////////////////////
		// /////////////////// 构造xsd schema ///////////////////
		// //////////////////////////////////////////////////////////
		XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
		XmlSchema xmlschema = new XmlSchema(targetNsUri, xmlSchemaCollection);
		xmlschema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
		xmlschema.setAttributeFormDefault(XmlSchemaForm.UNQUALIFIED);

		// /////////////////1、构造set 的schema //////////////////////
		Vector values = zi.getValues();
		Vector columns = zi.getColumns();
		if (columns==null || columns.size()==0){
			throw new ParseException("The insert sql style is not supported; column's names are expected. ");
		}

		int parameterIndex = 0;
		XmlSchemaSequence valueFieldsTypeSequence = new XmlSchemaSequence();
		for (int index = 0; index < values.size(); index++) {
			ZExp e = (ZExp) values.get(index);
			if (isPreparedColumn(e)) {
				parameterIndex++;
				int sqlType = parameterMetaData
						.getParameterType(parameterIndex);
				QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP.get(sqlType);

				XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
						false);

				fieldElement.setName((String)columns.get(index));

				fieldElement.setSchemaTypeName(type != null ? type
						: Constants.XSD_STRING);
				valueFieldsTypeSequence.getItems().add(fieldElement);
			}
		}
		XmlSchemaComplexType valueFieldsType = new XmlSchemaComplexType(
				xmlschema, true);
		valueFieldsType.setName(VALUES_ELEMENT + "Type");
		valueFieldsType.setParticle(valueFieldsTypeSequence);

		XmlSchemaElement valueFieldsElement = new XmlSchemaElement(xmlschema,
				true);
		valueFieldsElement.setName(VALUES_ELEMENT);
		valueFieldsElement.setSchemaTypeName(valueFieldsType.getQName());

		return xmlSchemaCollection;
	}

	protected static XmlSchemaCollection generateDeleteSchema(ZDelete zd,
			String sql, String targetNsUri, Connection conn)
			throws ParseException, SQLException {

		ParameterMetaData parameterMetaData = null;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		parameterMetaData = pstmt.getParameterMetaData();

		// //////////////////////////////////////////////////////////
		// /////////////////// 构造xsd schema ///////////////////
		// //////////////////////////////////////////////////////////
		XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
		XmlSchema xmlschema = new XmlSchema(targetNsUri, xmlSchemaCollection);
		xmlschema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
		xmlschema.setAttributeFormDefault(XmlSchemaForm.UNQUALIFIED);

		// /////////////// 1、构造删除条件的schema /////////////////
		ZExpression w = (ZExpression) zd.getWhere();
		List<String> pareparedColumns = new ArrayList<String>();
		if (w != null) {
			handleWhere(w, pareparedColumns);
		}

		XmlSchemaSequence whereFieldsTypeSequence = new XmlSchemaSequence();
		for (int i = 0; i < pareparedColumns.size(); i++) {
			String preparedColumn = pareparedColumns.get(i);
			XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
					false);
			fieldElement.setName(preparedColumn);
			QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP
					.get(parameterMetaData.getParameterType(i + 1));
			fieldElement.setSchemaTypeName(type != null ? type
					: Constants.XSD_STRING);
			whereFieldsTypeSequence.getItems().add(fieldElement);
		}

		XmlSchemaComplexType whereFieldsType = new XmlSchemaComplexType(
				xmlschema, true);
		whereFieldsType.setName(WHERE_ELEMENT + "Type");
		whereFieldsType.setParticle(whereFieldsTypeSequence);

		XmlSchemaElement whereFieldsElement = new XmlSchemaElement(xmlschema,
				true);
		whereFieldsElement.setName(WHERE_ELEMENT);
		whereFieldsElement.setSchemaTypeName(whereFieldsType.getQName());

		return xmlSchemaCollection;
	}

	protected static XmlSchemaCollection generateQuerySchema(ZQuery zq,
			String sql, String targetNsUri, Connection conn)
			throws ParseException, SQLException {

		ParameterMetaData parameterMetaData = null;
		ResultSetMetaData rsMetaData = null;

		PreparedStatement pstmt = conn.prepareStatement(sql);
		parameterMetaData = pstmt.getParameterMetaData();
		rsMetaData = pstmt.getMetaData();

		// //////////////////////////////////////////////////////////
		// /////////////////// 构造xsd schema ///////////////////
		// //////////////////////////////////////////////////////////
		XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
		XmlSchema xmlschema = new XmlSchema(targetNsUri, xmlSchemaCollection);
		xmlschema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
		xmlschema.setAttributeFormDefault(XmlSchemaForm.UNQUALIFIED);

		// /////////////// 1、构造查询条件的schema /////////////////
		ZExpression w = (ZExpression) zq.getWhere();
		List<String> pareparedColumns = new ArrayList<String>();
		if (w != null) {
			handleWhere(w, pareparedColumns);
		}

		XmlSchemaSequence whereFieldsTypeSequence = new XmlSchemaSequence();
		for (int i = 0; i < pareparedColumns.size(); i++) {
			String preparedColumn = pareparedColumns.get(i);
			XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
					false);
			fieldElement.setName(preparedColumn);
			QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP
					.get(parameterMetaData.getParameterType(i + 1));
			fieldElement.setSchemaTypeName(type != null ? type
					: Constants.XSD_STRING);
			whereFieldsTypeSequence.getItems().add(fieldElement);
		}

		XmlSchemaComplexType whereFieldsType = new XmlSchemaComplexType(
				xmlschema, true);
		whereFieldsType.setName(WHERE_ELEMENT + "Type");
		whereFieldsType.setParticle(whereFieldsTypeSequence);

		XmlSchemaElement whereFieldsElement = new XmlSchemaElement(xmlschema,
				true);
		whereFieldsElement.setName(WHERE_ELEMENT);
		whereFieldsElement.setSchemaTypeName(whereFieldsType.getQName());

		// /////////////////// 2、构造查询结果的schema /////////////////////
		Vector sel = zq.getSelect();
		XmlSchemaSequence rowSequence = new XmlSchemaSequence();
		int columnCount = rsMetaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			XmlSchemaElement fieldElement = new XmlSchemaElement(xmlschema,
					false);
			fieldElement.setName(rsMetaData.getColumnName(i).toLowerCase());
			QName type = DataMapper.SQLTYPES_2_XSD_QNAME_MAP.get(rsMetaData
					.getColumnType(i));
			fieldElement.setSchemaTypeName(type != null ? type
					: Constants.XSD_STRING);
			rowSequence.getItems().add(fieldElement);
		}

		// 1.1行类型行元素
		XmlSchemaComplexType datarowType = new XmlSchemaComplexType(xmlschema,
				true);
		datarowType.setName(ROW_ELEMENT + "Type");
		datarowType.setParticle(rowSequence);

		XmlSchemaElement rowElement = new XmlSchemaElement(xmlschema, true);
		rowElement.setName(ROW_ELEMENT);
		rowElement.setSchemaTypeName(datarowType.getQName());

		// 1.2data_set类型及data_set元素
		XmlSchemaElement rowElement_inner = new XmlSchemaElement(xmlschema,
				false);
		rowElement_inner.setName(ROW_ELEMENT);
		rowElement_inner.setSchemaTypeName(datarowType.getQName());
		XmlSchemaSequence datasetSequence = new XmlSchemaSequence();
		datasetSequence.getItems().add(rowElement_inner);

		XmlSchemaComplexType datasetType = new XmlSchemaComplexType(xmlschema,
				true);
		datasetType.setName(DATA_SET_ELEMENT + "Type");
		datasetType.setParticle(datasetSequence);

		XmlSchemaElement datasetElement = new XmlSchemaElement(xmlschema, true);
		datasetElement.setName(DATA_SET_ELEMENT);
		datasetElement.setSchemaTypeName(datasetType.getQName());

		// 构造schema结束
		return xmlSchemaCollection;
	}

	protected static void handleWhere(ZExp e, List<String> pareparedColumns) {

		// if(meta != null) System.out.println(meta);

		if (!(e instanceof ZExpression))
			return;
		ZExpression w = (ZExpression) e;

		Vector operands = w.getOperands();
		if (operands == null)
			return;

		// Look for prepared column ("?")
		String prepared = null;
		for (int i = 0; i < operands.size(); i++) {
			if (isPreparedColumn((ZExp) operands.elementAt(i))) {
				prepared = ((ZConstant) operands.elementAt(0)).getValue();
				// if (operands.size() != 2) {
				// throw new Exception("ERROR in where clause ?? found:"
				// + w.toString());
				// }
				break;
			}
		}

		if (prepared != null) { // prepared contains the (raw) column or alias
								// name
			pareparedColumns.add(prepared);

		} else { // No prepared column, go further analyzing the expression

			for (int i = 0; i < operands.size(); i++) {
				handleWhere(w.getOperand(i), pareparedColumns); // WARNING -
																// Recursive
				// call...
			}
		}

	}

	protected static boolean isPreparedColumn(ZExp v) {
		return (v instanceof ZExpression && ((ZExpression) v).getOperator()
				.equals("?"));
	}

}
