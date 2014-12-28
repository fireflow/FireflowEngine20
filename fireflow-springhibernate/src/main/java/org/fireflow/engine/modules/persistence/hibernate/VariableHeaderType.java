/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.modules.persistence.hibernate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.EqualsHelper;

/**
 * 将Map<String,String>转换成xml方式存储 <map> <entry> <key></key> <value></value>
 * </entry> <map>
 * 
 * @author Administrator
 * 
 */
public class VariableHeaderType implements UserType {
	private static final Log log = LogFactory.getLog(VariableHeaderType.class);
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
	
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		return (Serializable)arg0;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return EqualsHelper.equals(x, y);
	}

	public int hashCode(Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}


	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		String s = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		return this.xmlString2Map(s);
	}


	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		Hibernate.STRING.nullSafeSet(st, this.map2XmlString((Properties)value), index);

	}


	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return arg0;
	}

	public Class returnedClass() {
		return Map.class;
	}


	public int[] sqlTypes() {
		// TODO Auto-generated method stub
		return TYPES;
	}

}
