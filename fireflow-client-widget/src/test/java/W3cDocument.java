import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class W3cDocument {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document w3cdoc = null;

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setCoalescing(true);
			DocumentBuilder parser = factory.newDocumentBuilder();

			w3cdoc = parser.newDocument();
			
			Element root = w3cdoc.createElement("foo");
			root.setAttribute("attr1", "abc&def>123");
			w3cdoc.appendChild(root);
			/*
			String xmlFromDom4j = getXml(w3cdoc,"UTF-8");
			System.out.println(xmlFromDom4j);
			*/
			Transformer tf = TransformerFactory.newInstance().newTransformer();

			tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			

			StreamResult dest = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(w3cdoc); 
			tf.transform(source, dest);

			String xml = dest.getWriter().toString();
			//xml = xml.replaceAll("&amp;", "&");
			System.out.println(xml);
			
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}
	public static String getXml(org.w3c.dom.Document w3cDoc,String encoding)
	{
		try{
	        org.dom4j.io.DOMReader   xmlReader   =   new   org.dom4j.io.DOMReader(); 
	        org.dom4j.Document dom4jDoc = xmlReader.read(w3cDoc); 
			
	        //输出格式化器
	        OutputFormat format = new OutputFormat();//("    ", true);
	        //设置编码
	        format.setEncoding(encoding);
	        //format.setOmitEncoding(true);
	        format.setSuppressDeclaration(true);
	        //xml输出器
	        StringWriter out = new StringWriter();
	        XMLWriter xmlWriter = new XMLWriter(out, format);
	        xmlWriter.setEscapeText(true);

	        //打印doc
	        xmlWriter.write(dom4jDoc);
	        xmlWriter.flush();
	        //关闭输出器的流，即是printWriter
			String xml = out.toString();
			out.close();
			return xml;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}

	}
}
