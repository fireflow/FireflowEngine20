package org.fireflow.engine.modules.persistence.hibernate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.firesoa.common.util.Utils;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;

public class ClobMapType extends ClobStringType {
	private static DocumentFactory documentFactory = new DocumentFactory();
	private static final Log log = LogFactory.getLog(ClobMapType.class);
	
	public Map<String,String> xmlString2Map(String theString){
		String s = (String) theString;
		if (s == null || s.trim().equals(""))
			return new HashMap<String, String>();
		String encoding = Utils.findXmlCharset(theString);
		
		Map map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		reader.setEncoding(encoding);
		try {
			Document doc = reader.read(new ByteArrayInputStream(s
					.getBytes(encoding)));
			Element theMapElement = doc.getRootElement();
			List<Element> entryElements = theMapElement.elements("entry");
			if (entryElements != null) {
				for (Element entryElm : entryElements) {
					Element key = entryElm.element("key");
					Element value = entryElm.element("value");
					map.put(key.getText(), value.getText());
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		} catch (DocumentException e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
	
	public String map2XmlString(Map<String,String> arg0){
		if (arg0 == null)
			return null;
		Map<String, String> map = (Map<String, String>) arg0;
		Document document = documentFactory.createDocument();
		Element root = documentFactory.createElement("map");
		document.setRootElement(root);

		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = map.get(key);
			Element entry = documentFactory.createElement("entry");
			root.add(entry);
			Element keyElm = documentFactory.createElement("key");
			entry.add(keyElm);
			keyElm.setText(key);

			Element valueElm = documentFactory.createElement("value");
			entry.add(valueElm);

			valueElm.add(documentFactory.createCDATA(value));
		}

		try {
			StringWriter writer = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(Charset.defaultCharset().name());

			XMLWriter xmlwriter = new XMLWriter(writer, format);
			xmlwriter.write(document);
			return writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document.toString();
	}
	
	protected Object nullSafeGetInternal(
			ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
			throws SQLException {

		String s = lobHandler.getClobAsString(rs, names[0]);
		return this.xmlString2Map(s);
	}

	protected void nullSafeSetInternal(
			PreparedStatement ps, int index, Object value, LobCreator lobCreator)
			throws SQLException {
		String s = this.map2XmlString((Map<String,String>)value);
		lobCreator.setClobAsString(ps, index, s);
	}
	
	public Class returnedClass() {
		return Map.class;
	}
}
