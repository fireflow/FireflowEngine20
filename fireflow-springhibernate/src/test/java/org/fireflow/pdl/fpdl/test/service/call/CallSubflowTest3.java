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
package org.fireflow.pdl.fpdl.test.service.call;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

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
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


/**
 * 本测试主要验证子流程调用后，token.processInstanceId，token.elementInstanceId
 * 与ActivityInstance.tokenId，ProcessInstance.tokenId的匹配性。
 * 以及主流程子流程之间的阐述传递正确性。
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallSubflowTest3 extends FireWorkflowJunitEnviroment {
	protected static final String processName = "My_New_Process21";
	protected static final String processDisplayName = "我的新流程21";
	protected static final String description = "";
	protected static final String bizId = "biz_123";
	protected WorkflowProcess process2 = null;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);

		//执行主流程
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {

				//构建流程定义
				WorkflowProcess process = getWorkflowProcess();
				
				Map<String,Object> vars = new HashMap<String,Object>();
				vars.put("a", new Integer(5));
				vars.put("b", new Integer(6));
				//启动流程
				try {
					ProcessInstance processInstance = stmt.startProcess(process, bizId, vars);
					
					if (processInstance!=null){
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

	public void assertResult(WorkflowSession session) {
		super.assertResult(session);
		
		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		int tokenListSize = q4Token.count();
		Assert.assertEquals(12, tokenListSize);
		
		q4Token.add(Restrictions.eq(TokenProperty.ELEMENT_ID, processName+WorkflowProcess.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME));
		List<Token> mainProcInstTokenList = q4Token.list();
		Assert.assertNotNull(mainProcInstTokenList);
		Assert.assertEquals(1, mainProcInstTokenList.size());
		
		Token token4MainProcInst = mainProcInstTokenList.get(0);
		Assert.assertEquals(processInstanceId, token4MainProcInst.getProcessInstanceId());
		Assert.assertEquals(processInstanceId, token4MainProcInst.getElementInstanceId());
		
		WorkflowQuery<ProcessInstance> q4ProcInst = session.createWorkflowQuery(ProcessInstance.class);
		ProcessInstance mainProcInst = q4ProcInst.get(processInstanceId);
		
		Assert.assertNotNull(mainProcInst.getTokenId());
		Assert.assertEquals(mainProcInst.getTokenId(), token4MainProcInst.getId());
		
		q4Token.reset();
		q4Token.add(Restrictions.eq(TokenProperty.PARENT_TOKEN_ID, token4MainProcInst.getId()));
		List<Token> nodeTokenList4MainProc = q4Token.list();
		Assert.assertNotNull(nodeTokenList4MainProc);
		Assert.assertEquals(5, nodeTokenList4MainProc.size());
		
		WorkflowQuery<ActivityInstance> q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		for (Token tmp:nodeTokenList4MainProc){
			if (tmp.getElementInstanceId()!=null){
				q4ActInst.reset();
				q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.TOKEN_ID, tmp.getId()));
				List<ActivityInstance> actInstList = q4ActInst.list();
				Assert.assertNotNull(actInstList);
				
				Assert.assertEquals(1, actInstList.size());
				
				ActivityInstance actInst = actInstList.get(0);
				
				Assert.assertEquals(tmp.getElementInstanceId(), actInst.getId());
			}
		}
		
		q4Token.reset();
		q4Token.add(Restrictions.eq(TokenProperty.ELEMENT_ID, processName+WorkflowProcess.ID_SEPARATOR+"SubProcess2"));
		List<Token> subProcTokenList = q4Token.list();
		Assert.assertNotNull(subProcTokenList);
		Assert.assertEquals(1, subProcTokenList.size());
		Token subProcToken = subProcTokenList.get(0);
		
		q4ProcInst.reset();
		q4ProcInst.add(Restrictions.eq(ProcessInstanceProperty.SUBPROCESS_ID, 
				processName+WorkflowProcess.ID_SEPARATOR+"SubProcess2"));
		List<ProcessInstance> subProcInstList = q4ProcInst.list();
		Assert.assertNotNull(subProcInstList);
		Assert.assertEquals(1, subProcInstList.size());
		ProcessInstance procInst = subProcInstList.get(0);
		
		Assert.assertEquals(subProcToken.getProcessInstanceId(), procInst.getId());
		Assert.assertEquals(procInst.getTokenId(), subProcToken.getId());
		
		q4Token.reset();
		q4Token.add(Restrictions.eq(TokenProperty.PARENT_TOKEN_ID, subProcToken.getId()));
		List<Token> nodeTokenList4SubProc = q4Token.list();
		Assert.assertNotNull(nodeTokenList4SubProc);
		Assert.assertEquals(5, nodeTokenList4SubProc.size());
		
		for (Token tmp:nodeTokenList4SubProc){
			if (tmp.getElementInstanceId()!=null){
				q4ActInst.reset();
				q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.TOKEN_ID, tmp.getId()));
				List<ActivityInstance> actInstList = q4ActInst.list();
				Assert.assertNotNull(actInstList);
				
				Assert.assertEquals(1, actInstList.size());
				
				ActivityInstance actInst = actInstList.get(0);
				
				Assert.assertEquals(tmp.getElementInstanceId(), actInst.getId());
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		InputStream inStream = this.getClass().getResourceAsStream("CallSubflowTest3.f20.xml");
		FPDLDeserializer des = new FPDLDeserializer();
		WorkflowProcess process = null;
		try {
			process = des.deserialize(inStream);
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
