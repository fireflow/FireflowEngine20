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
package org.firesoa.common.schema;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Assert;
import org.w3c.dom.Document;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class Temp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String nsUri = "http://www.example.org/QualifiedSchema";
		
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("QualifiedSchema.xsd");
//		byte[] content = new byte[inStream.available()];
//		
//		inStream.read(content);
//		System.out.println(new String(content));
		
		StreamSource source = new StreamSource(inStream);
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
//					Assert.assertNull(schema.getElementByName(new QName(nsUri,"Person")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Person"), false);
		System.out.println( DOMInitializer.dom2String(generated));   

	}

}
