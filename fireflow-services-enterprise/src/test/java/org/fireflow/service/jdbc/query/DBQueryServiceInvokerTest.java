package org.fireflow.service.jdbc.query;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.AbsTestContext;
import org.firesoa.common.schema.SQLSchemaGenerator;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class DBQueryServiceInvokerTest  extends AbsTestContext{
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;

	@Test
	public void testInvoke() throws Exception{
		final DBQueryServiceDef service = (DBQueryServiceDef)this.buildService1();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("doQuery",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(1, inputs.size());
		Assert.assertEquals(new QName(service.getTargetNamespaceUri(),SQLSchemaGenerator.WHERE_ELEMENT),inputs.get(0).getDataType());
		Assert.assertEquals("arg0",inputs.get(0).getName());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(1, outputs.size());
		Assert.assertEquals(new QName(service.getTargetNamespaceUri(),SQLSchemaGenerator.DATA_SET_ELEMENT), outputs.get(0).getDataType());
		
		//
		TransactionTemplate transactionTemplate1 = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate1.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try{
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					
					//构造流程变量
					procInst.setVariableValue(session, "process_id", "processId-123456");
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造局部流程变量
					actInst.setVariableValue(session, "state", "0");
				}catch(Exception e){

					
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}

				
				return null;
			}
		});
		
		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					

					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("doQuery");
//					serviceBinding.setOperation(operations.get(0));

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					

					
					AssignmentImpl inputAssignment = new AssignmentImpl();
					//貌似org.apache.commons.beanutils.MethodUtils.invokeMethod不能用null,做参数
					String xpathBody = "/activityVars/state";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(xpathBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0/ns0:Where/ns0:state");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					toExpression.getNamespaceMap().put("ns0", operations.get(0).getInputs().get(0).getDataType().getNamespaceURI());
					inputAssignments.add(inputAssignment);
					
					//arg0
					inputAssignment = new AssignmentImpl();
					//貌似org.apache.commons.beanutils.MethodUtils.invokeMethod不能用null,做参数
					String jsBody = "/processVars/process_id";
					 exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0/ns0:Where/ns0:process_id");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					toExpression.getNamespaceMap().put("ns0", operations.get(0).getInputs().get(0).getDataType().getNamespaceURI());
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);
					
					//构造输出映射
					List<Assignment> outputAssignments = new ArrayList<Assignment>();
					
					//  1、 输出到实例级别的流程变量
					AssignmentImpl outputAssignment = new AssignmentImpl();
					ExpressionImpl expression = new ExpressionImpl();
					expression.setLanguage("xpath");
					expression.setBody("/outputs/"+operations.get(0).getOutputs().get(0).getName());
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/processVars/result");
					toExpression.setDataType(operations.get(0).getOutputs().get(0).getDataType());
					
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
		

		//验证
		//1、验证processInstance中是否有result
		Document doc = (Document)procInst.getVariableValue(session, "result");
		Assert.assertNotNull(doc);
		Element dataSetElement = doc.getDocumentElement();
		NodeList rowNodeList = dataSetElement.getElementsByTagName("Row");
		Assert.assertEquals(1, rowNodeList.getLength());
		Element rowElement = (Element)rowNodeList.item(0);
		Assert.assertNotNull(rowElement);
		NodeList idNodeList = rowElement.getElementsByTagName("id");
		Assert.assertEquals(1, idNodeList.getLength());
		Element idElement = (Element)idNodeList.item(0);
		Assert.assertNotNull(idElement);
		Assert.assertNotNull(idElement.getTextContent());
	}
	
	public ServiceDef buildService1() throws Exception{
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		DBQueryServiceDef service = new DBQueryServiceDef();
		service.setName("query_from_workitem");
		service.setTargetNamespaceUri("http://test/");
		service.setDataSource(ds);
		service.setSQL("select * from t_ff_rt_process_instance where process_id=? and state=? ");
		
		service.afterPropertiesSet();
		return service;
	}
}
