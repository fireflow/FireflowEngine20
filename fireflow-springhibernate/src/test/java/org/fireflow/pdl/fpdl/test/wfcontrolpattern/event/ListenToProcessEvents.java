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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.event;

import java.io.IOException;
import java.io.InputStream;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ListenToProcessEvents extends FireWorkflowJunitEnviroment {

	protected static final String processName = "ListenToProcessEvents";
	protected static final String processDisplayName = "监听流程运行时事件";
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
	/**
	 * 读取流程“并行汇聚死锁_1.f20.xml”
	 */
	public WorkflowProcess createWorkflowProcess() {
		InputStream in = this.getClass().getResourceAsStream(
				"监听流程运行时事件.f20.xml");

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
