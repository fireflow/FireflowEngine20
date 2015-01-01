package org.fireflow.engine.entity.nutz;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.Utils;
import org.nutz.dao.jdbc.ValueAdaptor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class VariablePayloadValueAdaptor implements ValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		//TODO mysql 直接用getstring,其他类型的数据库待改进
		String s = rs.getString(colName);
		
		String headerXml = rs.getString(VariableProperty.HEADERS.name());
		String dataType = rs.getString(VariableProperty.DATA_TYPE.name());
		//String uri = "http://jcp.org/en/jsr/detail?id=270";
		String uri = NameSpaces.JAVA.getUri();
		if (dataType!=null && dataType.startsWith("{"+uri+"}")){
			//java类型
			XStream xstream = new XStream(new DomDriver());			
			return xstream.fromXML(s);
		}else{
			//xml类型
			Properties headers = VariableHeaderValueAdaptor.xmlString2Map(headerXml);
			String className = (String)headers.get(Variable.HEADER_KEY_CLASS_NAME);
			String encoding = (String)headers.get(Variable.HEADER_KEY_ENCODING);
			
			if (StringUtils.isEmpty(encoding)){
				encoding = Utils.findXmlCharset(s);
			}
			try{
				ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(encoding));
				SAXReader reader = new SAXReader();
				reader.setEncoding(encoding);
				Document dom4jDoc = reader.read(in);
				
				if (StringUtils.isEmpty(className) || className.trim().equals("org.w3c.dom.Document")){
					DOMWriter domWriter = new DOMWriter();
					org.w3c.dom.Document dom = domWriter.write(dom4jDoc);
					return dom;
				}else{
					return dom4jDoc;
				}
			}catch(Exception e){
				throw new SQLException(e.getMessage());
			}

		}
	}

	public void set(PreparedStatement stat, Object value, int index)
			throws SQLException {
		String s = null;
		//System.out.println("++Inside VariablePalyloadType.nullSageSetInternal():: value is "+value+",dataType is "+(value==null?"null":value.getClass().getName()));
		if (value!=null && value instanceof org.w3c.dom.Document){
			org.w3c.dom.Document w3cDom = (org.w3c.dom.Document)value;
			DOMReader reader = new DOMReader();
			org.dom4j.Document dom4jDoc = reader.read(w3cDom);
			s = dom4jDoc.asXML();
		}else if (value!=null && value instanceof org.dom4j.Document){
			org.dom4j.Document dom4jDoc = (org.dom4j.Document)value;
			s = dom4jDoc.asXML();
		}else{
			XStream xstream = new XStream();
			s = xstream.toXML(value);
		}
		
		//TODO mysql 数据库，按照string写入,其他类型的数据库需要再改进

		stat.setString(index, s);
	}

}
