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
package org.fireflow.pdl.bpel;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.BaseEnviroment4Junit;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.bpel.basic.EmptyActivity;
import org.fireflow.pdl.bpel.basic.XYZActivity;
import org.fireflow.pdl.bpel.structure.If;
import org.fireflow.pdl.bpel.structure.Sequence;
import org.fireflow.pdl.bpel.structure.While;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @author 非也
 * @version 2.0
 */
public class WhileTest extends BaseEnviroment4Junit {
	protected static final String processId = "TheWhileProcess";
	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(PROCESS_TYPE);
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
		
				//构建流程定义
				BpelProcess process = new BpelProcess(processId);
				process.setStartActivity((new While("While","processVars.x<3",
														(new Sequence("While.Seq1"))
															.addChild((new If("While.Seq1.If","processVars.y==1",new EmptyActivity("EmptyActivity_In_If_1")))
																			.addElseIf("processVars.y==2", new EmptyActivity("EmptyActivity_In_If_2"))
																			.setElse(new EmptyActivity("EmptyActivity_In_If_3")))
															.addChild(new XYZActivity("While.Seq1.XYZActivity"))
														)
										));
				
				//启动流程
				try {
					Map<String, Object> vars = new HashMap<String, Object>();
					vars.put("x", new Integer(1));
					vars.put("y", new Integer(2));

					stmt.startProcess(process, null, vars);

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
	}
}
