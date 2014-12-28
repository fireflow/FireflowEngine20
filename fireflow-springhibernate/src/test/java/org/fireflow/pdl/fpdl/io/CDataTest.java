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
package org.fireflow.pdl.fpdl.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class CDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc0 = docBuilder.newDocument();
		//本行特别重要，指示解析器是否转换特殊字符.
		final Node pi = 
			doc0.createProcessingInstruction(StreamResult.PI_DISABLE_OUTPUT_ESCAPING,"");

		doc0.appendChild(pi);

		// 准备数据
		String data0 = "Line1\r\n\r\nLine2& <>";

		// 产生doc
		Element root0 = doc0.createElement("root");
		doc0.appendChild(root0);
		CDATASection cdata = doc0.createCDATASection(data0);
		root0.appendChild(cdata);

		//方案1 输出doc
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(doc0), new StreamResult(out));
		out.flush();
		
		String xmlStr1 = out.toString("UTF-8");
		System.out.println("=====方案1输出的xml是========"+xmlStr1.length());
		System.out.println(xmlStr1);
		

		// 读取方案1的Doc输出
		ByteArrayInputStream in1 = new ByteArrayInputStream(xmlStr1.getBytes("UTF-8"));
		Document doc1 = docBuilder.parse(in1);
		Element root1 = doc1.getDocumentElement();
		String data1 = loadCDATA(root1);
		System.out.println("==========重新读取方案1的输出==================");
		System.out.println("===========data0 长度是："+data0.length()+"内容是：");
		System.out.println(data0);
		System.out.println("===========data1 长度是："+data1.length()+"内容是：");
		System.out.println(data1);
		
		//方案2输出
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();             
		DOMImplementationLS impl =                                                                
		    (DOMImplementationLS)registry.getDOMImplementation("LS");         

		LSSerializer writer = impl.createLSSerializer(); 
		String xmlStr2 = writer.writeToString(doc0);    
		System.out.println("=====方案2输出的xml是========"+xmlStr2.length());
		System.out.println(xmlStr2);
		
		//读取方案2的输出
		ByteArrayInputStream in2 = new ByteArrayInputStream(xmlStr2.getBytes("UTF-16"));
		Document doc2 = docBuilder.parse(in2);
		Element root2 = doc2.getDocumentElement();
		String data2 = loadCDATA(root2);
		System.out.println("==========重新读取方案1的输出==================");
		System.out.println("===========data0 长度是："+data0.length()+"内容是：");
		System.out.println(data0);
		System.out.println("===========data2 长度是："+data2.length()+"内容是：");
		System.out.println(data2);
	}
	
	protected static String loadCDATA(Element cdataElement){
		if (cdataElement==null){
			return "";
		}else{
			NodeList nodeList = cdataElement.getChildNodes();
			if(nodeList!=null && nodeList.getLength()>0){
				int length = nodeList.getLength();
				for (int i=0;i<length;i++){
					org.w3c.dom.Node node = nodeList.item(i);
					if(node.getNodeType()==org.w3c.dom.Node.CDATA_SECTION_NODE){
						CDATASection cdataSection = (CDATASection)node;
						System.out.println("=================textcontent is ========="+cdataSection.getTextContent().length());
						System.out.println(cdataSection.getTextContent());
						System.out.println("=================node value is==================="+cdataSection.getNodeValue().length());
						System.out.println(cdataSection.getNodeValue());
						System.out.println("====================whole text content is "+cdataSection.getWholeText().length());
						System.out.println(cdataSection.getWholeText());
						System.out.println("==================data is ========="+cdataSection.getWholeText().length());
						System.out.println(cdataSection.getData());
						System.out.println("=====================================");
//						return cdataSection.getData();
					}
				}
			}
			System.out.println("--------cdataElement getTextContent -----");
			System.out.println(cdataElement.getTextContent());
			System.out.println("--------cdataElement getNodeValue -----");
			System.out.println(cdataElement.getNodeValue());
			return cdataElement.getTextContent();
		}
	}

}
