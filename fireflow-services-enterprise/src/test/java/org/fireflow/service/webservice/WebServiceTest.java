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
package org.fireflow.service.webservice;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.fireflow.service.webservice.servicemock.HelloWorldImpl_1;
import org.fireflow.service.webservice.servicemock3.HelloWorldImpl_3;
import org.junit.Test;

/**
 * 检查Webservice解析wsdl的正确性
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WebServiceTest {
	/**
	 * 检查最简单的wsdl，xsd schema完全集成在wsdl文件中
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct1_1() throws Exception{
		String url = "classpath:/org/fireflow/service/webservice/servicemock/HelloWorld.wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://servicemock.webservice.service.fireflow.org/","sayHello");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}
	}
	
	/**
	 * 通过http获取wsdl，xsd 和wsdl分开不同文件中，通过http获取
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct1_2() throws Exception{
		HelloWorldImpl_1 helloWorld = new HelloWorldImpl_1();
		String address = "http://localhost:9001/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);

		
		String url = "http://localhost:9001/HelloWorld?wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();

		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
//		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://servicemock.webservice.service.fireflow.org/","sayHello");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}

		endpoint.stop();
	}
	
	/**
	 * 检查最简单的wsdl，xsd schema和wsdl在不同文件中，通过classpath获取
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct2_1() throws Exception{
		String url = "classpath:/org/fireflow/service/webservice/servicemock2/HelloWorld.wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();

		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
//		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://servicemock.webservice.service.fireflow.org/","sayHello");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}
	}
	
	/**
	 * 检查最简单的wsdl，多个xsd schema和wsdl在不同文件中，通过http获取
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct3_1() throws Exception{
		HelloWorldImpl_3 helloWorld = new HelloWorldImpl_3();
		String address = "http://localhost:9002/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);
		
		String url = "http://localhost:9002/HelloWorld?wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();

		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
//		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://common.model.webservice.fireflow.org/","sex");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			qname = new QName("http://model.webservice.fireflow.org/","address2");
			xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}
		
		endpoint.stop();
	}
	
	/**
	 * 检查最简单的wsdl，多个xsd schema和wsdl在同一文件中，通过classpath获取
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct4_1() throws Exception{
		String url = "classpath:/org/fireflow/service/webservice/servicemock4/HelloWorld_all_inline.wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();

		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
//		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://common.model.hw.demo/","sex");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			qname = new QName("http://model.hw.demo/","address");
			xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}
	}
	
	/**
	 * 检查最简单的wsdl，多个xsd schema和wsdl在不同文件中，wsdl通过classpath获取，schema通过http获取
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostConstruct4_2() throws Exception{
		HelloWorldImpl_3 helloWorld = new HelloWorldImpl_3();
		String address = "http://localhost:9002/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);
		
		String url = "classpath:/org/fireflow/service/webservice/servicemock4/HelloWorld.wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");
		
		ws.afterPropertiesSet();
		System.out.println("===============接口定义如下===================");
		System.out.println(ws.getInterface().toString());
		
		System.out.println("===============类型定义如下===================");
		XmlSchemaCollection schemaCollection = ws.getXmlSchemaCollection();

		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
//		Assert.assertEquals(2, schemas.length);
		for (XmlSchema schema:schemas){
			QName qname = new QName("http://common.model.webservice.fireflow.org/","sex");
			XmlSchemaElement xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			qname = new QName("http://model.webservice.fireflow.org/","address2");
			xmlSchemaElement = schemaCollection.getElementByQName(qname);
			Assert.assertNotNull(xmlSchemaElement);
			
			if (!"http://www.w3.org/2001/XMLSchema".equals(schema.getTargetNamespace())){
				schema.write(System.out);
			}
			
		}
		
		endpoint.stop();
	}
}
