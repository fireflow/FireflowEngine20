package org.fireflow.engine.entity.nutz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import org.nutz.dao.jdbc.ValueAdaptor;

public class VariableHeaderValueAdaptor implements ValueAdaptor {
	private static final Log log = LogFactory.getLog(VariableHeaderValueAdaptor.class);
	private static DocumentFactory documentFactory = new DocumentFactory();
	private static final int[] TYPES = new int[] { Types.VARCHAR };

	public static Properties xmlString2Map(String theString){
		String s = (String) theString;
		if (s == null || s.trim().equals(""))
			return new Properties();
		
		String encoding = Utils.findXmlCharset(theString);
		
		Properties map = new Properties();
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
	
	public static String map2XmlString(Properties arg0){
		if (arg0 == null)
			return null;
		Properties map =  arg0;
		Document document = documentFactory.createDocument();
		Element root = documentFactory.createElement("map");
		document.setRootElement(root);

		Iterator<Object> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String)keys.next();
			String value = (String)map.get(key);
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
			
			String jvmEncoding = Charset.defaultCharset().name();
			format.setEncoding(jvmEncoding);

			XMLWriter xmlwriter = new XMLWriter(writer, format);
			xmlwriter.write(document);
			return writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document.toString();
	}
	

	public Object get(ResultSet rs, String colName) throws SQLException {
		String s = (String) rs.getString(colName);

		return this.xmlString2Map(s);
	}

	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		stat.setString(index, this.map2XmlString((Properties)obj));
	}

}
