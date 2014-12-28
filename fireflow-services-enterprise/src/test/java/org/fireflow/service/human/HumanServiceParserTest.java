package org.fireflow.service.human;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.DocumentException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class HumanServiceParserTest {

	@Test
	public void testHumanServiceParser() throws Exception{

		try {
			List<ServiceDef> services = this.buildServices();
//			Document doc = ServiceParser.serializeToDOM(services);
//			doc.getDocumentElement().addNamespace(NameSpaces.JAVA.getPrefix(), NameSpaces.JAVA.getUri());
//			String xml_0 = doc.asXML();
//			System.out.println(xml_0);
			System.out.println("==================================================");
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
				Assert.assertEquals(((CommonInterfaceDef)svc1_2.getInterface()).getOperations().size(),
						((CommonInterfaceDef)svc2_2.getInterface()).getOperations().size());
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
		HumanService svc = new HumanService();
		svc.setName("Human_service_1");
		svc.setDisplayName("打开XYZ表单");
		svc.setDescription("This is a human service");
		svc.setBizCategory("abc\\xyz");
		svc.setInvokerBeanName("testInvokerBean");
		svc.setFormUrl("/xyz.jsp");
		
		//构建operation
		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
		OperationDefImpl op = new OperationDefImpl();
		op.setOperationName("OpenForm");
		InputImpl input = new InputImpl();
		input.setName("bizObj1");
		input.setName("单据1");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.junit.Abc",NameSpaces.JAVA.getPrefix()));
		op.getInputs().add(input);
		
		input = new InputImpl();
		input.setName("todate");
		input.setDisplayName("当前日期");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.util.Date",NameSpaces.JAVA.getPrefix()));
		input.setDataPattern("yyyy-MM-dd");
		input.setDefaultValueAsString("2011-09-20");
		op.getInputs().add(input);
		
		commonInterface.getOperations().add(op);
		
		svc.setInterface(commonInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		/////////////service2//////////////////////////
		svc = new HumanService();
		svc.setName("human_service_2");
		svc.setDisplayName("调用XYZ功能");
		svc.setDescription("This is a human service");
		svc.setBizCategory("abc\\xyz");

		
		commonInterface = new CommonInterfaceDef();
		op = new OperationDefImpl();
		op.setOperationName("OpenForm");
		input = new InputImpl();
		input.setName("bizObj2");
		input.setName("单据2");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.junit.XYZ",NameSpaces.JAVA.getPrefix()));
		op.getInputs().add(input);
		commonInterface.getOperations().add(op);
		
		svc.setInterface(commonInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		return list;
	}
}
