package org.fireflow.service.java;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.SAXReader;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class JavaServiceParserTest {

	@Test
	public void testJavaServiceParser()throws Exception {
		try {
			List<ServiceDef> services = this.buildServices();
			
			String xml = ServiceParser.serializeToXmlString(services);
			System.out.println(xml);
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			try {
				DocumentBuilder documentBuilder = docBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
				
				List<ServiceDef> services2 = ServiceParser.deserialize(document);
				Assert.assertNotNull(services2);
				Assert.assertEquals(services.size(), services2.size());
				
				ServiceDef svc1_2 = services.get(1);
				
				ServiceDef svc2_2 = services2.get(1);
				
				Assert.assertEquals(svc1_2.getId(), svc2_2.getId());
				Assert.assertEquals(svc1_2.getDisplayName(), svc2_2.getDisplayName());
				Assert.assertEquals(svc1_2.getBizCategory(), svc2_2.getBizCategory());
				Assert.assertEquals(svc1_2.getDescription(), svc2_2.getDescription());
				Assert.assertEquals(svc1_2.getInvokerBeanName(), svc2_2.getInvokerBeanName());
				Assert.assertEquals(((JavaInterfaceDef)svc1_2.getInterface()).getInterfaceClassName(),
						((JavaInterfaceDef)svc2_2.getInterface()).getInterfaceClassName());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
//			DocumentFactory df = new DocumentFactory();
//			Document ddf.createDocument("UTF-8");
			
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	protected List<ServiceDef> buildServices(){
		List<ServiceDef> list = new ArrayList<ServiceDef>();
		
		////////////service1//////////////////////////
		JavaService svc = new JavaService();
		svc.setName("java_service_1");
		svc.setDisplayName("调用XYZ功能");
		svc.setDescription("This is a java service");
		svc.setBizCategory("abc\\xyz");
		svc.setInvokerBeanName("testInvokerBean");
		svc.setJavaBeanName("testJavaBean");
		svc.setJavaClassName("org.firesoa.test.Test");
		
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.firesoa.test.Test");
		svc.setInterface(javaInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		/////////////service2//////////////////////////
		svc = new JavaService();
		svc.setName("java_service_2");
		svc.setDisplayName("调用XYZ功能");
		svc.setDescription("This is a java service");
		svc.setBizCategory("abc\\xyz");
		svc.setInvokerBeanName("testInvokerBean");
		svc.setJavaBeanName("testJavaBean");
		svc.setJavaClassName("org.firesoa.test.Test");
		
		javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.firesoa.test.Test");
		svc.setInterface(javaInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		return list;
	}
}
