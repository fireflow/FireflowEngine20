package org.fireflow;

import java.text.SimpleDateFormat;
import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobProperty;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */

/**
 * @author 非也
 * @version 2.0
 */
public class BaseEnviroment4Junit {
	protected static final String PROCESS_TYPE = "BPEL";
	protected static final String springConfig = "applicationContext.xml";
	protected static Resource resource = null;
	protected static XmlBeanFactory beanFactory = null;
	protected static RuntimeContext runtimeContext = null;
	protected static TransactionTemplate transactionTemplate = null;

	protected static String processInstanceId = null;

	protected static SimpleDateFormat formater = new SimpleDateFormat(
			"yyyyMMdd HH:mm:ss");

	/**
	 * 初始化引擎
	 */
	@BeforeClass
	public static void beforeClass() {
		resource = new ClassPathResource(springConfig);
		beanFactory = new XmlBeanFactory(resource);
		runtimeContext = (RuntimeContext) beanFactory.getBean("runtimeContext");
		transactionTemplate = (TransactionTemplate) beanFactory
				.getBean("springTransactionTemplate");
		final JunitInitializer initializer = (JunitInitializer) beanFactory
				.getBean("junitInitializer");

		// 清除现有的数据
		transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {

				initializer.clearData();
				return null;
			}

		});

		final WorkflowSession session = WorkflowSessionFactory
				.createWorkflowSession(runtimeContext, FireWorkflowSystem
						.getInstance());
		final WorkflowStatement stmt = session
				.createWorkflowStatement(PROCESS_TYPE);

		// 发布缺省的资源定义文件
		transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {
				try {
//					InputStream in = BaseEnviroment4Junit.class
//							.getClassLoader().getResourceAsStream(
//									"FireWorkflow-Default-Resources.rsc.xml");
//					Map<ResourceDescriptorProperty, Object> props = new HashMap<ResourceDescriptorProperty, Object>();
//					props.put(ResourceDescriptorProperty.FILE_NAME,
//							"FireWorkflow-Default-Resources.rsc.xml");

//					stmt.uploadResourcesStream(in, Boolean.TRUE, props);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		});

	}

	@AfterClass
	public static void afterClass() {
		// 等待调度器结束
		Scheduler scheduler = runtimeContext.getEngineModule(Scheduler.class,
				PROCESS_TYPE);
		boolean hasJobInSchedule = scheduler.hasJobInSchedule(runtimeContext);
		System.out.println();
		while (hasJobInSchedule) {
			System.out.print("...");
			try {
				Thread.currentThread().sleep(3 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hasJobInSchedule = scheduler.hasJobInSchedule(runtimeContext);
		}
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(
				runtimeContext, FireWorkflowSystem.getInstance());

		WorkflowQuery<ProcessInstance> q4ProcInst = session
				.createWorkflowQuery(ProcessInstance.class);
		List<ProcessInstance> processInstanceList = q4ProcInst.list();

		WorkflowQuery<ActivityInstance> q4ActInst = session
				.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.addOrder(
				Order.asc(ActivityInstanceProperty.PROCESS_INSTANCE_ID))
				.addOrder(Order.asc(ActivityInstanceProperty.STEP_NUMBER));
		List<ActivityInstance> activityInstanceList = q4ActInst.list();

		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		q4Token.addOrder(Order.asc(TokenProperty.PROCESS_INSTANCE_ID))
				.addOrder(Order.asc(TokenProperty.STEP_NUMBER));
		List<Token> tokenList = q4Token.list();

		WorkflowQuery<Variable> q4Variable = session.createWorkflowQuery(
				Variable.class);
		q4Variable.addOrder(Order.asc(VariableProperty.SCOPE_ID));
		List<Variable> variableList = q4Variable.list();

		WorkflowQuery<WorkItem> q4WorkItem = session.createWorkflowQuery(
				WorkItem.class);
		q4WorkItem
				.addOrder(
						Order
								.asc(WorkItemProperty.PROCESSS_ID))
				.addOrder(
						Order
								.asc(WorkItemProperty.PROCESS_INSTANCE_ID))
				.addOrder(Order.asc(WorkItemProperty.ACTIVITY_INSTANCE_$_ID));
		List<WorkItem> workItemList = q4WorkItem.list();

		WorkflowQuery<ScheduleJob> q4ScheduleJob = session.createWorkflowQuery(
				ScheduleJob.class);
		q4ScheduleJob
				.addOrder(Order.asc(ScheduleJobProperty.PROCESS_ID))
				.addOrder(
						Order
								.asc(ScheduleJobProperty.ACTIVITY_INSTANCE_$_STEP_NUMBER));
		List<ScheduleJob> jobList = q4ScheduleJob.list();

		// //////////////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////////////

		System.out
				.println("******************************************************");
		System.out
				.println("**********Process Instance Staroge Content***********");
		System.out
				.println("******************************************************");
		System.out.println("Id\t\tState\t\tName\t\tDisplayName\t\t");

		for (ProcessInstance procInst : processInstanceList) {
			System.out.print(procInst.getId());
			System.out.print("\t\t");
			System.out.print(procInst.getState().getDisplayName());
			System.out.print("\t\t");
			System.out.print(procInst.getProcessName());
			System.out.print("\t\t");
			System.out.print(procInst.getProcessDisplayName());

			System.out.println("\n");
		}

		System.out
				.println("******************************************************");
		System.out
				.println("**********Activity Instance Staroge Content***********");
		System.out
				.println("******************************************************");
		System.out
				.println("StepNumber\t\tState\t\tName\t\tDisplayName\t\tProcessInstanceId\t\tId\t\t");

		for (ActivityInstance actInst : activityInstanceList) {

			System.out.print(actInst.getStepNumber());

			System.out.print("\t\t");
			System.out.print(actInst.getState().getDisplayName());
			System.out.print("\t\t");
			System.out.print(actInst.getName());
			System.out.print("\t\t");
			System.out.print(actInst.getDisplayName());
			System.out.print("\t\t");

			System.out.print(actInst.getProcessInstanceId());
			System.out.print("\t\t");
			System.out.print(actInst.getId());
			System.out.println("\n");
		}

		System.out
				.println("******************************************************");
		System.out
				.println("**************Token Staroge Content*******************");
		System.out
				.println("******************************************************");
		System.out
				.println("StepNumber\tState\tNodeId\t\tOperationContext\t\tProcessId\t\tProcessInstanceId\t\tId");
		for (Token token : tokenList) {
			System.out.print(token.getStepNumber());
			System.out.print("\t");

			System.out.print(token.getState().getDisplayName());
			System.out.print("\t");

			System.out.print(token.getElementId());
			System.out.print("\t\t");
			System.out.print(token.getOperationContextName().name());
			System.out.print("\t\t");
			System.out.print(token.getProcessId());
			System.out.print("\t\t");
			System.out.print(token.getProcessInstanceId());
			System.out.print("\t\t");
			System.out.print(token.getId());

			System.out.println("\n");
		}

		System.out
				.println("******************************************************");
		System.out
				.println("**************Variable Staroge Content****************");
		System.out
				.println("******************************************************");
		System.out.println("ScopeId\t\tName\t\tDataType\t\tValue");

		for (Variable var : variableList) {
			System.out.print(var.getScopeId());
			System.out.print("\t\t");
			System.out.print(var.getName());
			System.out.print("\t\t");
			System.out.print(var.getDataType());
			System.out.print("\t\t");
			System.out.print(var.getPayload());
			System.out.println("\n");
		}

		System.out
				.println("******************************************************");
		System.out
				.println("**************WorkItem  Staroge Content  *************");
		System.out
				.println("******************************************************");
		System.out
				.println("Name\t\tState\t\tOwner\t\tCreatedTime\t\tClaimedTime\t\tEndTime\t\tId\ttActivityInstanceId");

		for (WorkItem workItem : workItemList) {
			System.out.print(workItem.getSubject());
			System.out.print("\t\t");
			System.out.print(workItem.getState().getDisplayName());
			System.out.print("\t\t");
			System.out.print(workItem.getOwnerName());
			System.out.print("\t\t");
			System.out.print(workItem.getCreatedTime() == null ? "--"
					: formater.format(workItem.getCreatedTime()));
			System.out.print("\t\t");
			System.out.print(workItem.getClaimedTime() == null ? "--"
					: formater.format(workItem.getClaimedTime()));
			System.out.print("\t\t");
			System.out.print(workItem.getEndTime() == null ? "--" : formater
					.format(workItem.getEndTime()));
			System.out.print("\t\t");
			System.out.print(workItem.getId());
			System.out.print("\t\t");
			ActivityInstance actInst = ((AbsWorkItem)workItem).getActivityInstance();
			System.out.print(actInst==null?actInst.getId():"-");
			System.out.println();
		}

		System.out
				.println("******************************************************");
		System.out
				.println("**************ScheduleJob Staroge Content*************");
		System.out
				.println("******************************************************");
		System.out
				.println("Name\t\tTriggeredTimes\t\tLastestTriggeredTime\t\tState\t\tEndTime\t\tTiggerType\t\tTriggerExpression");

		for (ScheduleJob job : jobList) {
			System.out.print(job.getName());
			System.out.print("\t\t");
			System.out.print(job.getTriggeredTimes());
			System.out.print("\t\t");
			System.out.print(job.getLatestTriggeredTime() == null ? "--"
					: formater.format(job.getLatestTriggeredTime()));
			System.out.print("\t\t");
			System.out.print(job.getState().getDisplayName());
			System.out.print("\t\t");
			System.out.print(job.getEndTime() == null ? "--" : formater
					.format(job.getEndTime()));
			System.out.print("\t\t");
			System.out.print(job.getTriggerType());
			System.out.print("\t\t");
			System.out.print(job.getTriggerExpression());
			System.out.println();
		}
	}

	public void assertResult(WorkflowSession session) {
		Assert.assertNotNull(processInstanceId);
	}


}
