package org.fireflow.service.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;
import org.firesoa.common.schema.SQLSchemaGenerator;
import org.gibello.zql.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class AbstractDBService extends AbstractServiceDef {

	protected static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();

	/**
	 * 查询SQL语句 select * from t_abc where id=:id
	 */
	protected String sql = null;

	/**
	 * 数据源
	 */
	protected DataSource dataSource = null;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSQL() {
		return sql;
	}

	public void setSQL(String theSQL) {
		if (!theSQL.trim().endsWith(";")) {
			this.sql = theSQL + ";";
		} else {
			this.sql = theSQL;
		}

	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		generateSchema();//
	}

	protected void generateSchema() throws ParseException, SQLException {
		Connection conn = this.getDataSource().getConnection();
		try {
			XmlSchemaCollection xmlSchemaCollection = SQLSchemaGenerator
					.generateXmlSchemaCollectionForSQL(this.getSQL(),
							this.getTargetNamespaceUri(), conn);
			this.setXmlSchemaCollection(xmlSchemaCollection);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}

	}

	/**
	 * 填充PreparedStatement
	 * 
	 * @param pstmt
	 * @param parameterElements
	 * @return 返回下次填充的起始位置
	 * @throws SQLException
	 * @throws ServiceInvocationException
	 */
	public static int fulfillPreparedStatement(PreparedStatement pstmt,
			List<Element> parameterElements, int startIndex)
			throws SQLException, ServiceInvocationException {
		ParameterMetaData parameterMetaData = pstmt.getParameterMetaData();
		int preparedParamCount = parameterMetaData.getParameterCount();

		int paramCount = parameterElements.size();
		int currentParamIndex = 0;
		if (preparedParamCount > 0) {
			for (int i = 0; i < paramCount; i++) {
				currentParamIndex = i + startIndex;
				int type = parameterMetaData
						.getParameterType(currentParamIndex);
				Element element =  parameterElements.get(i);
				String strValue = null;
				NodeList nodeList = element.getChildNodes();
				for (int k=0;k<nodeList.getLength();k++){
					Node node = nodeList.item(k);
					if (node.getNodeType()==Node.TEXT_NODE){
						strValue = ((Text)node).getNodeValue();
					}
				}

				switch (type) {

				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.LONGVARCHAR:
				//case java.sql.Types.NCHAR:
				//case java.sql.Types.NVARCHAR:
				//case java.sql.Types.LONGNVARCHAR:
					pstmt.setString(currentParamIndex, strValue);
					break;

				case java.sql.Types.NUMERIC:
				case java.sql.Types.DECIMAL:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						if (strValue.indexOf(".") == -1) {
							Long theLong = Long.valueOf(strValue);
							pstmt.setBigDecimal(currentParamIndex,
									BigDecimal.valueOf(theLong));

						} else {
							Double d = Double.valueOf(strValue);
							pstmt.setBigDecimal(currentParamIndex,
									BigDecimal.valueOf(d));
						}
					}

					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);

					} else {
						pstmt.setBoolean(currentParamIndex,
								Boolean.valueOf(strValue));
					}

					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.INTEGER:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						pstmt.setInt(currentParamIndex,
								Integer.valueOf(strValue));
					}

					break;

				case java.sql.Types.BIGINT:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						pstmt.setLong(currentParamIndex, Long.valueOf(strValue));
					}

					break;

				case java.sql.Types.REAL:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						pstmt.setFloat(currentParamIndex,
								Float.valueOf(strValue));
					}

					break;

				case java.sql.Types.FLOAT:
				case java.sql.Types.DOUBLE:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						pstmt.setDouble(currentParamIndex,
								Double.valueOf(strValue));
					}

					break;

				case java.sql.Types.BINARY:
				case java.sql.Types.VARBINARY:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.CLOB:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						byte[] content = null;
						try {
							content = Base64.decodeBase64(strValue
									.getBytes("UTF-8"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (content == null) {
							pstmt.setNull(currentParamIndex, type);
						} else {
							pstmt.setBytes(currentParamIndex, content);
						}

					}

					break;

				case java.sql.Types.DATE:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						java.sql.Date sqlDate = java.sql.Date.valueOf(strValue);
						pstmt.setDate(currentParamIndex, sqlDate);
					}

					break;

				case java.sql.Types.TIME:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						java.sql.Time t = java.sql.Time.valueOf(strValue);
						pstmt.setTime(currentParamIndex, t);
					}

					break;

				case java.sql.Types.TIMESTAMP:
					if (StringUtils.isEmpty(strValue)) {
						pstmt.setNull(currentParamIndex, type);
					} else {
						String dt = strValue;
						if (dt.indexOf("T") >= 0) {// 如果是“YYYY-MM-DDThh:mm:ss”这种格式，则将T替换成空格
							dt = dt.replace("T", " ");
						}
						java.sql.Timestamp ts = Timestamp.valueOf(dt);
						pstmt.setTimestamp(currentParamIndex, ts);
					}

					break;

				}
			}
		}
		return currentParamIndex + 1;
	}

	public static Document createDataSet(ResultSet rs,
			ResultSetMetaData metaData, String targetNamespace)
			throws SQLException, IOException {
		DocumentBuilder builder = null;

		try {
			builder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document doc = builder.newDocument();

		Element rootElement = doc.createElementNS(targetNamespace,
				SQLSchemaGenerator.DATA_SET_ELEMENT);

		doc.appendChild(rootElement);

		while (rs.next()) {
			createRow(rs, metaData, rootElement);
		}

		return doc;
	}

	protected static Element createRow(ResultSet rs,
			ResultSetMetaData metaData, Element rootElement)
			throws SQLException, IOException {
		Document doc = rootElement.getOwnerDocument();
		
		Element rowElement = doc.createElement(SQLSchemaGenerator.ROW_ELEMENT);
		rootElement.appendChild(rowElement);

		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			int type = metaData.getColumnType(i);
			String columnName = metaData.getColumnName(i);
			if (columnName==null)columnName="";
			Element colElement = doc.createElement(columnName.toLowerCase());
			rowElement.appendChild(colElement);

			switch (type) {
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			//case java.sql.Types.NCHAR:
			//case java.sql.Types.NVARCHAR:
			//case java.sql.Types.LONGNVARCHAR:
				String content = rs.getString(i);
				colElement.appendChild(doc.createTextNode(content == null ? "" : content));
				break;

			case java.sql.Types.NUMERIC:
			case java.sql.Types.DECIMAL:
				int scale = metaData.getScale(i);
				if (scale <= 0) {// 按照整数处理
					Long l = rs.getLong(i);
					colElement.appendChild(doc.createTextNode(l == null ? "" : l.toString()));
				} else {// 按照double处理
					Double d = rs.getDouble(i);
					colElement.appendChild(doc.createTextNode(d == null ? "" : d.toString()));
				}
				break;

			case java.sql.Types.BOOLEAN:
			case java.sql.Types.BIT:
				Boolean b = rs.getBoolean(i);
				colElement.appendChild(doc.createTextNode(b == null ? "" : b.toString()));
				break;

			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
				Integer theInt = rs.getInt(i);
				colElement.appendChild(doc.createTextNode(theInt == null ? "" : theInt.toString()));
				break;

			case java.sql.Types.BIGINT:
				Long theLong = rs.getLong(i);
				colElement.appendChild(doc.createTextNode(theLong == null ? "" : theLong.toString()));
				break;

			case java.sql.Types.REAL:
				Float f = rs.getFloat(i);
				colElement.appendChild(doc.createTextNode((f == null ? "" : f.toString())));
				break;

			case java.sql.Types.FLOAT:
			case java.sql.Types.DOUBLE:
				Double db = rs.getDouble(i);
				colElement.appendChild(doc.createTextNode(db == null ? "" : db.toString()));
				break;

			case java.sql.Types.BINARY:
			case java.sql.Types.VARBINARY:
			case java.sql.Types.LONGVARBINARY:
				byte[] bytes = rs.getBytes(i);
				if (bytes != null) {
					String base64 = new String(Base64.encodeBase64(bytes),"UTF-8");
					colElement.appendChild(doc.createCDATASection(base64));
				}

			case java.sql.Types.BLOB:
				Blob blob = rs.getBlob(i);
				if (blob != null) {
					InputStream in = blob.getBinaryStream();
					if (in != null) {
						byte[] tyeBytes = new byte[in.available()];
						int offset = 0;
						int numRead = 0;
						while (offset < tyeBytes.length
								&& (numRead = in.read(tyeBytes, offset,
										tyeBytes.length - offset)) >= 0) {
							offset += numRead;
						}
						String base64_2 = new String(Base64.encodeBase64(tyeBytes),"UTF-8");
						colElement.appendChild(doc.createCDATASection(base64_2));
					}
				}

				break;

			case java.sql.Types.CLOB:
				Clob clob = rs.getClob(i);
				if (clob != null) {
					InputStream clobIn = clob.getAsciiStream();
					if (clobIn != null) {
						byte[] clobBytes = new byte[clobIn.available()];
						int clob_offset = 0;
						int clob_numRead = 0;
						while (clob_offset < clobBytes.length
								&& (clob_numRead = clobIn.read(clobBytes,
										clob_offset, clobBytes.length
												- clob_offset)) >= 0) {
							clob_offset += clob_numRead;
						}
						String base64_3 = new String(Base64.encodeBase64(clobBytes),"UTF-8");
						colElement.appendChild(doc.createCDATASection(base64_3));
					}
				}

				break;

			case java.sql.Types.DATE:
				java.sql.Date sqlDate = rs.getDate(i);
				colElement.appendChild(doc.createTextNode(sqlDate == null ? "" : sqlDate.toString()));
				break;

			case java.sql.Types.TIME:
				java.sql.Time t = rs.getTime(i);
				colElement.appendChild(doc.createTextNode(t == null ? "" : t.toString()));
				break;

			case java.sql.Types.TIMESTAMP:
				java.sql.Timestamp ts = rs.getTimestamp(i);
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss");
				colElement.appendChild(doc.createTextNode(ts == null ? "" : df.format(ts)));
				break;
			}

		}

		return rowElement;
	}

}
