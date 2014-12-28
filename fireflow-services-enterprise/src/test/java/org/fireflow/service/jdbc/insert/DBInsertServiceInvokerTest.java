package org.fireflow.service.jdbc.insert;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
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
@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class DBInsertServiceInvokerTest extends AbsTestContext{

	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	
	VarBean varBean = new VarBean();

	@Test
	public void testInvoke() throws Exception{
		final DBInsertServiceDef service = (DBInsertServiceDef)this.buildService1();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("doInsert",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(1, inputs.size());
		Assert.assertEquals(new QName(service.getTargetNamespaceUri(),SQLSchemaGenerator.VALUES_ELEMENT),inputs.get(0).getDataType());
		Assert.assertEquals("arg0",inputs.get(0).getName());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(0, outputs.size());
		
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
					procInst.setVariableValue(session, "varBean", varBean);
					
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
					serviceBinding.setOperationName("doInsert");
//					serviceBinding.setOperation(operations.get(0));

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					//arg0
					AssignmentImpl inputAssignment = new AssignmentImpl();
					//貌似org.apache.commons.beanutils.MethodUtils.invokeMethod不能用null,做参数
					String xpathBody = "/processVars/varBean/processType";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(xpathBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0/ns0:Values/ns0:PROCESS_TYPE");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					toExpression.getNamespaceMap().put("ns0", operations.get(0).getInputs().get(0).getDataType().getNamespaceURI());
					inputAssignments.add(inputAssignment);
					
					
					inputAssignment = new AssignmentImpl();
					//貌似org.apache.commons.beanutils.MethodUtils.invokeMethod不能用null,做参数
					String jsBody = "/processVars/varBean/version";
					exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0/ns0:Values/ns0:VERSION");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					toExpression.getNamespaceMap().put("ns0", operations.get(0).getInputs().get(0).getDataType().getNamespaceURI());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					

					
					serviceBinding.setInputAssignments(inputAssignments);
					
					
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
		WorkflowQuery<Variable> q = session.createWorkflowQuery(Variable.class);
		Variable var = q.add(Restrictions.eq(VariableProperty.ID, "Id123")).unique();
		Assert.assertNotNull(var);
		Assert.assertEquals(Integer.valueOf(varBean.getVersion()),var.getVersion() );
		Assert.assertEquals(varBean.getProcessType(),var.getProcessType() );

	}
	
	public ServiceDef buildService1() throws Exception{
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		DBInsertServiceDef service = new DBInsertServiceDef();
		service.setName("insert_process_vars");
		service.setTargetNamespaceUri("http://test/");
		service.setDataSource(ds);
		service.setSQL("insert into t_ff_rt_variable(ID,SCOPE_ID,NAME,PROCESS_ELEMENT_ID,HEADERS,DATA_TYPE,PAYLOAD,PROCESS_ID,VERSION,PROCESS_TYPE) values('Id123','scopeId123','testVar','PROCESS_ELEMENT_ID123','<?xml version=\"1.0\" encoding=\"UTF-8\"?><map/>','{http://jcp.org/en/jsr/detail?id=270}java.lang.String','<string>playload123</string>','processId123',?,?)");
		
		service.afterPropertiesSet();
		return service;
	}
}
