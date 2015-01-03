package org.fireflow.engine.modules.persistence.hibernate;

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
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;

import com.thoughtworks.xstream.XStream;

public class VariablePayloadType extends ClobStringType {
	private static final String DATA_TYPE_FIELD_NAME = "DATA_TYPE";
	private static final String HEADERS_FIELD_NAME = "HEADERS";
	

	protected Object nullSafeGetInternal(
			ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
			throws SQLException {
		
		String s = lobHandler.getClobAsString(rs, names[0]);
		
		String headerXml = rs.getString(5);//配置文件，Headers必须在第五个字段//rs.getString(VariableProperty.HEADERS.name());
		String dataType = rs.getString(6);//DataType必须在第六个字段//rs.getString(VariableProperty.DATA_TYPE.name());
		//String uri = "http://jcp.org/en/jsr/detail?id=270";
		String uri = NameSpaces.JAVA.getUri();
		if (dataType!=null && dataType.startsWith("{"+uri+"}")){
			//java类型
			XStream xstream = new XStream();			
			return xstream.fromXML(s);
		}else{
			//xml类型
			Properties headers = VariableHeaderType.xmlString2Map(headerXml);
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

	protected void nullSafeSetInternal(
			PreparedStatement ps, int index, Object value, LobCreator lobCreator)
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

		lobCreator.setClobAsString(ps, index, s);
	}
	
	public Class returnedClass() {
		return Object.class;
	}
}
