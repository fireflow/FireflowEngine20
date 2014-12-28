package org.fireflow.service.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.OutputAssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.service.AbsTestContext;
import org.fireflow.service.webservice.servicemock.Address;
import org.fireflow.service.webservice.servicemock.HelloWorldImpl_1;
import org.fireflow.service.webservice.servicemock.Person;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class WebServiceInvokerTest extends AbsTestContext{
	private static final Log log = LogFactory.getLog(WebServiceInvokerTest.class);
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	
	@BeforeClass
	public static void startupWebService(){
//		HelloWorldImpl helloWorld = new HelloWorldImpl();
//		String address = "http://localhost:9000/helloWorld";
//		Endpoint endpoint = Endpoint.publish(address, helloWorld);
	}
	
	
	@Test
	public void testInvoke() throws Exception{
		HelloWorldImpl_1 helloWorld = new HelloWorldImpl_1();
		String address = "http://localhost:9001/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);
		
		final WebServiceDef service = buildService1();
		log.info("Service的接口定义如下：");
		log.info(service.getInterface().toString());
		
		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					
					//构造流程变量
					Address address = new Address();
					address.setCityName("广州");
					address.setStreetName("中山大道中274号");
					Person p = new Person();
					p.setAddress(address);
					p.setName("非也");
					p.setSex("Boy");					
					procInst.setVariableValue(session, "person", p);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
			
					
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					//构造service binding
					OperationDef operation = service.getInterface().getOperation("sayHello");
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("sayHello");
//					serviceBinding.setOperation(operation);

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					//parameters part
					AssignmentImpl inputAssignment = new AssignmentImpl();
					String jsBody = "/processVars/person/name";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/parameters/ns0:sayHello/person/name");
					toExpression.setDataType(operation.getInputs().get(0).getDataType());
					toExpression.getNamespaceMap().put("ns0", "http://servicemock.webservice.service.fireflow.org/");
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//////
					inputAssignment = new AssignmentImpl();
					String xpathBody = new String("/processVars/person/sex");
					exp = new ExpressionImpl();
					exp.setBody(xpathBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/parameters/ns0:sayHello/person/sex");
					toExpression.setDataType(operation.getInputs().get(0).getDataType());
					toExpression.getNamespaceMap().put("ns0", "http://servicemock.webservice.service.fireflow.org/");
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//////
					inputAssignment = new AssignmentImpl();
					xpathBody = new String("/processVars/person/address/cityName");
					exp = new ExpressionImpl();
					exp.setBody(xpathBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/parameters/ns0:sayHello/person/address/cityName");
					toExpression.setDataType(operation.getInputs().get(0).getDataType());
					toExpression.getNamespaceMap().put("ns0", "http://servicemock.webservice.service.fireflow.org/");
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//////
					inputAssignment = new AssignmentImpl();
					xpathBody = new String("/processVars/person/address/streetName");
					exp = new ExpressionImpl();
					exp.setBody(xpathBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/parameters/ns0:sayHello/person/address/streetName");
					toExpression.setDataType(operation.getInputs().get(0).getDataType());
					toExpression.getNamespaceMap().put("ns0", "http://servicemock.webservice.service.fireflow.org/");
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);
					
					//构造输出映射
					List<Assignment> outputAssignments = new ArrayList<Assignment>();
					
					
					//  1、 输出到实例级别的流程变量
					OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
					ExpressionImpl expression = new ExpressionImpl();
					expression.setLanguage("xpath");
					expression.setBody("/outputs/"+operation.getOutputs().get(0).getName());
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/processVars/result");
					toExpression.setDataType(operation.getOutputs().get(0).getDataType());
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					serviceBinding.setOutputAssignments(outputAssignments);
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, null);
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}

				catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				catch(Exception e){
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				return null;
			}
			
		});
		WorkflowQuery<Variable> q = session.createWorkflowQuery(Variable.class);
		Variable tempVar = q.add(Restrictions.eq(VariableProperty.SCOPE_ID, procInst.getId()))
							.add(Restrictions.eq(VariableProperty.NAME, "result")).unique();
		Assert.assertNotNull(tempVar);
		Document w3cDom = (Document)tempVar.getPayload();
		Assert.assertNotNull(w3cDom);
		Element root = w3cDom.getDocumentElement();
		Element returnElem = (Element)root.getFirstChild();
		"Hello ,Mr/Ms 非也.".equals(returnElem.getTextContent());

		endpoint.stop();
	}

	@Test
	public void testInvoke2() {

	}
	
	public WebServiceDef buildService1()throws Exception{
		String url = "http://localhost:9001/HelloWorld?wsdl";
		WebServiceDef ws = new WebServiceDef();
		ws.setWsdlURL(url);
		ws.setName("HelloWorldService");
		ws.setPortName("HelloWorldPort");		
		ws.afterPropertiesSet();
		return ws;
	}
}
