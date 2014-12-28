package org.fireflow.service.jdbc.query;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.AbsTestContext;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class DBQueryImplTest extends AbsTestContext{

	@Test
	public void testDoQuery() throws Exception{
		
		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {

				RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
				WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
				createProcessInstance(session, runtimeContext);
				
				return null;
			}
		});
		
		DBQueryImpl dbQuery = new DBQueryImpl();
		ServiceDef service = this.buildService();
		dbQuery.setDBQueryService((DBQueryServiceDef)service);
		
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		DocumentBuilder db =df.newDocumentBuilder();
		Document whereDoc = db.newDocument();
		Element rootElement = whereDoc.createElementNS(service.getTargetNamespaceUri(),"where_fields");
		whereDoc.appendChild(rootElement);
		
		Element procIdElem = whereDoc.createElementNS(service.getTargetNamespaceUri(),"process_id");
		procIdElem.appendChild(whereDoc.createTextNode("processId-123456"));
		rootElement.appendChild(procIdElem);
		
		Element stateElem = whereDoc.createElementNS(service.getTargetNamespaceUri(), "state");
		stateElem.appendChild(whereDoc.createTextNode("0"));
		rootElement.appendChild(stateElem);
		
		Document resultDoc = dbQuery.doQuery(whereDoc);
		

		Assert.assertNotNull(resultDoc);
		
		Element resultRoot = resultDoc.getDocumentElement();
		Assert.assertNotNull(resultRoot);
		Element rowElement = (Element)resultRoot.getFirstChild();
		Assert.assertNotNull(rowElement);
		NodeList idNodeList = rowElement.getElementsByTagName("id");
		Assert.assertEquals(1,idNodeList.getLength());
		Element idElement = (Element)idNodeList.item(0);
		Assert.assertNotNull(idElement);
		Assert.assertNotNull(idElement.getTextContent());
		
		NodeList tokenIdNodeList = rowElement.getElementsByTagName("token_id");
		Assert.assertEquals(1, tokenIdNodeList.getLength());
		Element tokenIdElement = (Element)tokenIdNodeList.item(0);
		Assert.assertNotNull(tokenIdElement);
		Assert.assertEquals("tokenId-123", tokenIdElement.getTextContent());
		
		TransformerFactory  transformerFactory  =  TransformerFactory.newInstance();  
		Transformer transformer = transformerFactory.newTransformer();  
		
		transformer.setOutputProperty(OutputKeys.ENCODING,"GB2312");  
		transformer.setOutputProperty(OutputKeys.INDENT,"yes");  
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");  
		ByteArrayOutputStream  outputStream  =  new  ByteArrayOutputStream();  
		//transformer.transform()方法 将 XML Source转换为 Result  
		transformer.transform(new DOMSource(resultDoc), new StreamResult(outputStream));  
		System.out.println( outputStream.toString() );   
	}
	
	public ServiceDef buildService(){
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		DBQueryServiceDef service = new DBQueryServiceDef();
		service.setName("query_from_workitem");
		service.setTargetNamespaceUri("http://test/");
		service.setDataSource(ds);
		service.setSQL("select * from t_ff_rt_process_instance where process_id=? and state=? ");
		
		try {
			service.afterPropertiesSet();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//输出service 的schema
		XmlSchemaCollection xmlSchemaCollection = service.getXmlSchemaCollection();
		if (xmlSchemaCollection!=null){
			XmlSchema[] xmlSchemas = xmlSchemaCollection.getXmlSchemas();
			for (XmlSchema xmlSchema : xmlSchemas){
				try {
					xmlSchema.write(System.out);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		
		
		return service;
	}

}
