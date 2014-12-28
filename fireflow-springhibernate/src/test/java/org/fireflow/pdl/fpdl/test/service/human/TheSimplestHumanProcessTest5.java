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
package org.fireflow.pdl.fpdl.test.service.human;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 测试一个人工任务流程，该流程在模拟上报如下错误
 * java.sql.BatchUpdateException: data exception: string data, right truncation
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class TheSimplestHumanProcessTest5 extends FireWorkflowJunitEnviroment {

	protected static final String processName = "My_New_Process6";

	protected static final String bizId = "biz1234";

	@SuppressWarnings("unchecked")
	@Test
	public void testStartProcess() {
		final WorkflowSession session = WorkflowSessionFactory
				.createWorkflowSession(fireflowRuntimeContext, FireWorkflowSystem
						.getInstance());
		final WorkflowStatement stmt = session
				.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);

		// 1 首先上传流程定义和服务定义
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {		
				
				// 发布一条流程
				WorkflowProcess process = createWorkflowProcess();
				try {
					ProcessDescriptor descriptor = stmt.uploadProcessObject(process, 0);
					((ProcessDescriptorImpl)descriptor).setPublishState(true);
					stmt.updateProcessDescriptor(descriptor);
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		});

		// 2 然后运行流程
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {

				// 启动流程
				try {
					Map<String,Object> vars = new HashMap<String,Object>();
					vars.put("applicant", "张三");
					vars.put("money", new Float("1234.0"));
					ProcessInstance processInstance = stmt.startProcess(
							processName, "My_New_Process6.main",bizId, vars);

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

	public static void main(String[] args){
		try {
			
			InputStream in = TheSimplestHumanProcessTest5.class.getResourceAsStream("TheSimplestHumanProcessTest5.f20.xml");

			FPDLDeserializer parser = new FPDLDeserializer();

			WorkflowProcess process = parser.deserialize(in);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start-->Activity(human service)-->End
	 * 
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess() {
		// 从文件中读取流程定义
		try {
			
			InputStream in = this.getClass().getResourceAsStream("TheSimplestHumanProcessTest5.f20.xml");

			FPDLDeserializer parser = new FPDLDeserializer();

			WorkflowProcess process = parser.deserialize(in);

			return process;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void assertResult(WorkflowSession session) {
		super.assertResult(session);
	}
}
