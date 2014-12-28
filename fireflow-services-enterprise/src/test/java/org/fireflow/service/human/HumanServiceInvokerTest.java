package org.fireflow.service.human;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.model.resourcedef.impl.ResourceDefImpl;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.fireflow.service.AbsTestContext;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml", 
		"classpath:/org/fireflow/service/human/HumanServiceInvokerTest-context.xml"
                              })
public class HumanServiceInvokerTest  extends AbsTestContext {
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	

	@Test
	public void testInvoke() {
		final ServiceDef service = this.buildService();

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);

					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("OpenForm");
//					serviceBinding.setOperation(service.getInterface().getOperation("OpenForm"));
					//serviceBinding.setInputAssignments(assignments);//TODO human service的Assignment 待补充
					
					//构造resourceBinding
					ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
					resourceBinding.setAssignmentHandlerBeanName("myAssignmentHandler");
					resourceBinding.setName("指定的操作者");
					
					invoker.invoke(session, actInst, serviceBinding, resourceBinding, null);
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
				}
				return null;
			}
			
		});
		
		WorkflowStatement stmt = session.createWorkflowStatement();
		WorkflowQuery<WorkItem> query =  session.createWorkflowQuery(WorkItem.class);
		final WorkItem wi = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "zhangsan"))
						.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_ID, actInst.getId()))
						.unique();
		Assert.assertNotNull(wi);
		Assert.assertEquals(WorkItemState.INITIALIZED, wi.getState());
		Assert.assertEquals("/xyz.jsp", wi.getActionUrl());
		Assert.assertNotNull(((LocalWorkItem)wi).getActivityInstance());
		Assert.assertNotNull(wi.getCreatedTime());
		Assert.assertEquals(WorkItemAssignmentStrategy.ASSIGN_TO_ANY, ((LocalWorkItem)wi).getAssignmentStrategy());
		
		query.reset();
		WorkItem wi2 = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "Mgr_C"))
						.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_ID, actInst.getId()))
						.unique();
		Assert.assertNotNull(wi2);
		Assert.assertEquals(WorkItemState.READONLY, wi2.getState());

		//测试wi claim()
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try {
					WorkflowStatement statement = session.createWorkflowStatement();
					statement.claimWorkItem(wi.getId());
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		

		query.reset();
		query.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_$_ID, actInst.getId()));
		int workItemCount = query.count();		
		Assert.assertEquals(3, workItemCount);//两个抄送workitem,一个待处理workitem
		
		//测试 wi.complete()动作
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try {
					WorkflowStatement statement = session.createWorkflowStatement();
					statement.completeWorkItem(wi.getId(),null, null, null);
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	@Test
	public void testInvoke2() {
		final ServiceDef service = this.buildService();

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);

					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("OpenForm");
//					serviceBinding.setOperation(service.getInterface().getOperation("OpenForm"));
					//serviceBinding.setInputAssignments(assignments);//TODO human service的Assignment 待补充
					
					//构造resourceBinding
					ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
					resourceBinding.setAssignmentHandlerClassName("org.fireflow.service.human.mock.MyAssignmentHandler");
					resourceBinding.setName("指定的操作者");
					
					invoker.invoke(session, actInst, serviceBinding, resourceBinding, null);
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
				}
				return null;
			}
			
		});
		
		WorkflowStatement stmt = session.createWorkflowStatement();
		WorkflowQuery<WorkItem> query =  session.createWorkflowQuery(WorkItem.class);
		WorkItem wi = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "zhangsan"))
							.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, actInst.getProcessInstanceId()))
							.unique();
		Assert.assertNotNull(wi);
		Assert.assertEquals(WorkItemState.INITIALIZED, wi.getState());
		Assert.assertEquals("/xyz.jsp", wi.getActionUrl());
		Assert.assertNotNull(((LocalWorkItem)wi).getActivityInstance());
		Assert.assertNotNull(wi.getCreatedTime());
		Assert.assertEquals(WorkItemAssignmentStrategy.ASSIGN_TO_ANY, ((LocalWorkItem)wi).getAssignmentStrategy());
		
		query.reset();
		WorkItem wi2 = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "Mgr_C"))
						.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, actInst.getProcessInstanceId()))
						.unique();
		Assert.assertNotNull(wi2);
		Assert.assertEquals(WorkItemState.READONLY, wi2.getState());
	
	}
	
	@Test
	public void testInvoke3() {
		final ServiceDef service = this.buildService();

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					procInst.setVariableValue(session, "thePotentialOwner", "wanghaha");
					
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("OpenForm");
//					serviceBinding.setOperation(service.getInterface().getOperation("OpenForm"));
					//serviceBinding.setInputAssignments(assignments);//TODO human service的Assignment 待补充
					
					//构造resourceBinding
					ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
					ResourceDefImpl resource = new ResourceDefImpl();					
					resource.setName("coder");//码农
					resource.setDisplayName("码农");
					resource.setResourceType(ResourceType.ROLE);
					resourceBinding.addPotentialOwnerRef(resource.getId());
					
					resource = new ResourceDefImpl();					
					resource.setName("TestDept");
					resource.setDisplayName("测试部");
					resource.setResourceType(ResourceType.DEPARTMENT);
					resourceBinding.addPotentialOwnerRef(resource.getId());
					
					resource = new ResourceDefImpl();					
					resource.setName("limou");
					resource.setDisplayName("李某");
					resource.setResourceType(ResourceType.USER);
					resourceBinding.addPotentialOwnerRef(resource.getId());
					
					resource = new ResourceDefImpl();					
					resource.setName("processCreator");
					resource.setDisplayName("流程创建者");
					resource.setResourceType(ResourceType.PROCESS_INSTANCE_CREATOR);
					resourceBinding.addPotentialOwnerRef(resource.getId());
					
					resource = new ResourceDefImpl();					
					resource.setName("thePotentialOwner");
					resource.setDisplayName("流程变量所指用户");
					resource.setResourceType(ResourceType.VARIABLE_IMPLICATION);
					resourceBinding.addPotentialOwnerRef(resource.getId());
					
					//抄送
					resource = new ResourceDefImpl();					
					resource.setName("manager");
					resource.setDisplayName("部门经理");
					resource.setResourceType(ResourceType.ROLE);
					resourceBinding.addReaderRef(resource.getId());
					
					resourceBinding.setName("指定的操作者");
					
					invoker.invoke(session, actInst, serviceBinding, resourceBinding, null);
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
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		WorkflowStatement stmt = session.createWorkflowStatement();
		WorkflowQuery<WorkItem> query =  session.createWorkflowQuery(WorkItem.class);
		WorkItem wi = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "zhangsan"))
							.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, actInst.getProcessInstanceId()))
							.unique();
		Assert.assertNotNull(wi);
		Assert.assertEquals(WorkItemState.INITIALIZED, wi.getState());
		Assert.assertEquals("/xyz.jsp", wi.getActionUrl());
		Assert.assertNotNull(((LocalWorkItem)wi).getActivityInstance());
		Assert.assertNotNull(wi.getCreatedTime());
		Assert.assertEquals(WorkItemAssignmentStrategy.ASSIGN_TO_ANY, ((LocalWorkItem)wi).getAssignmentStrategy());
		
		query.reset();
		WorkItem wi2 = query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, "Mgr_C"))
						.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, actInst.getProcessInstanceId()))
						.unique();
		Assert.assertNotNull(wi2);
		Assert.assertEquals(WorkItemState.READONLY, wi2.getState());
	
	}
	
	protected ServiceDef buildService(){
		
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

		return svc;
	}
}
