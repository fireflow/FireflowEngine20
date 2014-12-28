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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;

import javax.sql.DataSource;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.w3c.dom.Document;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class DOMInitializerTest extends AbstractJUnit4SpringContextTests{
	
	private boolean validateGeneratedDOM(Document dom ,URL schemaUrl){
		try{
			//验证生成的xml是否符合Schema约束
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
			SchemaFactory factory = SchemaFactory.newInstance(language);  
			javax.xml.validation.Schema schema = factory.newSchema(schemaUrl);//Schema.xml改成您的schema文件名 后缀可以为xsd  
			Validator validator=schema.newValidator();  
			validator.validate(new DOMSource(dom));//patterns.xml改为您要验证的xml文件名  
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	@Test
	public void testQualified_Schema()throws Exception{
		System.out.println("=====testQualified_Schema()================================================");
		String nsUri = "http://www.example.org/Qualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("Qualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("Qualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Person")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Person"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}
	
	
	@Test
	public void testUnqualified_Schema()throws Exception{
		System.out.println("=====testUnqualified_Schema()================================================");

		String nsUri = "http://www.example.org/Unqualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("Unqualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("Unqualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Foo")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Foo"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}
	
	@Test
	public void testQualified_Unqualified()throws Exception{
		System.out.println("=====testQualified_Unqualified()================================================");

		String nsUri = "http://www.example.org/Qualified_UnQualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("Qualified_UnQualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("Qualified_UnQualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Person")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Person"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}
	
	@Test
	public void testUnQualified_Qualified_1()throws Exception{
		System.out.println("=====testUnQualified_Qualified_1()================================================");

		String nsUri = "http://www.example.org/UnQualified_Qualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("UnQualified_Qualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("UnQualified_Qualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Bar")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Bar"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}
	
	@Test
	public void testUnQualified_Qualified_2()throws Exception{
		System.out.println("=====testUnQualified_Qualified_2()================================================");

		String nsUri = "http://www.example.org/UnQualified_Qualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("UnQualified_Qualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("UnQualified_Qualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Foo")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Foo"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}	
	
	
	@Test
	public void testUnQualified_Unqualified()throws Exception{
		System.out.println("=====testUnQualified_Unqualified()================================================");

		String nsUri = "http://www.example.org/UnQualified_Unqualified_Schema";
		
		URL url = DOMInitializerTest.class.getResource("Unqualified_unqualified_Schema.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("Unqualified_unqualified_Schema.xsd");
		StreamSource source = new StreamSource(inStream);
		
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Bar")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Bar"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}	
	
	@Test
	public void testUnqualified_Circular_reference1()throws Exception{
		System.out.println("=====Unqualified_Circular_reference1()================================================");

		String nsUri = "http://www.example.org/Unqualified_Circular_reference1";
		
		URL url = DOMInitializerTest.class.getResource("Unqualified_Circular_reference1.xsd");
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("Unqualified_Circular_reference1.xsd");
		StreamSource source = new StreamSource(inStream);
		
		
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.setBaseUri(url.toExternalForm());
		schemaCollection.read(source);
		
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,"Bar1")));
				}
			}
		}
		
		Document generated = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,"Bar1"), false);
		Assert.assertTrue(validateGeneratedDOM(generated,url));
		System.out.println( DOMInitializer.dom2String(generated));  
	}	
	/**
	 * Test method for {@link org.firesoa.common.schema.DOMInitializer#generateDocument(org.apache.ws.commons.schema.XmlSchemaCollection, javax.xml.namespace.QName, boolean)}.
	 */

	public void testGenerateDocument()throws Exception {
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		Connection conn = ds.getConnection();
		String nsUri = "http://test/";
		
		String sql = "update t_ff_rt_workitem set created_time=?,end_time=?,owner_id='testUser' , owner_type=?  where id='123' and (owner_id like ? or created_time>? or state=? )";

		XmlSchemaCollection schemaCollection = 
			SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
//					schema.write(System.out);
					Assert.assertNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.WHERE_ELEMENT)));
					Assert.assertNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.SET_ELEMENT)));
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.UPDATE_ELEMENT)));
				}
			}
		}
		
		Document doc = DOMInitializer.generateDocument(schemaCollection, new QName(nsUri,SQLSchemaGenerator.UPDATE_ELEMENT), false);
		  
		System.out.println( DOMInitializer.dom2String(doc));   
	}


	public void testGenerateDocument2()throws Exception {
		InputStream inStream = DOMInitializerTest.class.getResourceAsStream("WorkflowProcessSchema-2.0.xsd");
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		schemaCollection.read(new StreamSource(inStream));
		
		QName workflowProcessQName = new QName("http://www.fireflow.org/schema/workflowprocess","workflow-process");
		
		Document doc = DOMInitializer.generateDocument(schemaCollection,workflowProcessQName, false);
		TransformerFactory  transformerFactory  =  TransformerFactory.newInstance();  
		Transformer transformer = transformerFactory.newTransformer();  
		
		transformer.setOutputProperty(OutputKeys.ENCODING,"GB2312");  
		transformer.setOutputProperty(OutputKeys.INDENT,"yes");  
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");  
		ByteArrayOutputStream  outputStream  =  new  ByteArrayOutputStream();  
		//transformer.transform()方法 将 XML Source转换为 Result  
		transformer.transform(new DOMSource(doc), new StreamResult(outputStream));  
		System.out.println( outputStream.toString() ); 
	}
}
