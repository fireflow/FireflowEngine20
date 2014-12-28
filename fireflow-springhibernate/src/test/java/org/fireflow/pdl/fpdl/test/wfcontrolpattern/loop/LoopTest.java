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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.loop;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pvm.kernel.Token;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class LoopTest extends FireWorkflowJunitEnviroment {

	protected static final String processName = "Loop";
	protected static final String processDisplayName = "循环";
	protected static final String bizId = "ThisIsAJunitTest";

	/**
	 * 并行汇聚死锁，所以流程执行完毕后，并行汇聚节点应该处于Initialized状态
	 */
	@Test
	public void testStartProcess() {
		final WorkflowSession session = WorkflowSessionFactory
				.createWorkflowSession(fireflowRuntimeContext,
						FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session
				.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {

				// 构建流程定义
				WorkflowProcess process = getWorkflowProcess();

				// 启动流程
				try {
					ProcessInstance processInstance = stmt.startProcess(
							process, bizId, null);

					if (processInstance != null) {
						processInstanceId = processInstance.getId();
					}

				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WorkflowProcessNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});

		assertResult(session);
	}

	public void assertResult(WorkflowSession session){
		super.assertResult(session);
		
		WorkflowQuery<ActivityInstance> q4Act = session.createWorkflowQuery(ActivityInstance.class);
		q4Act.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, "Loop.main.Activity1"));
		
		List<ActivityInstance> acts = q4Act.list();
		Assert.assertNotNull(acts);
		Assert.assertEquals(4, acts.size());
		
		WorkflowQuery<ProcessInstance> q4Proc = session.createWorkflowQuery(ProcessInstance.class);
		q4Proc.add(Restrictions.eq(ProcessInstanceProperty.SUBPROCESS_ID, "Loop.main"));
		ProcessInstance procInst = q4Proc.unique();
		Assert.assertNotNull(procInst);
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst.getState());
		
		q4Proc.reset();
		ProcessInstance procInst2 = q4Proc.get(processInstanceId);
		Assert.assertNotNull(procInst2);
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst2.getState());
		
	}
	/**
	 * 读取流程“并行汇聚死锁_1.f20.xml”
	 */
	public WorkflowProcess createWorkflowProcess() {
		InputStream in = this.getClass().getResourceAsStream(
				"循环.f20.xml");

		FPDLDeserializer des = new FPDLDeserializer();
		WorkflowProcess process = null;
		try {
			process = des.deserialize(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return process;
	}

}
