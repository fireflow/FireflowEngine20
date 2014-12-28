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
package org.fireflow.pdl.bpel.structure;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.pdl.bpel.BpelActivity;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;


/**
 * @author 非也
 * @version 2.0
 */
public class While extends StructureActivity  {
	public While(String name,String condition,BpelActivity bpelActivity){
		super(name);
		
		bpelActivity.setParent(this);
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody(condition);
		Child childActivity = new Child(exp,bpelActivity);
		this.addChild(childActivity);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		
		if (this.getChildren()==null || this.getChildren().size()==0){
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}else{
			Child child = this.getChildren().get(0);
			Expression exp = child.getConditionExpression();
			
			RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
			
//			VariableService varService = ctx.getEngineModule(VariableService.class, token.getProcessType());
//			Map<String,Variable> vars = varService.getVariables(token.getProcessType(), token.getProcessInstanceId());
//			
//			Map<String,Object> varValues = new HashMap<String,Object>();
//			if (vars!=null){
//				Iterator<Entry<String,Variable>> it = vars.entrySet().iterator();
//				while(it.hasNext()){
//					Entry<String,Variable> entry = it.next();
//					varValues.put(entry.getKey(), entry.getValue().getVariableValue());
//				}
//			}
//			
			//变量环境，包含流程变量和流程系统常量
//			Map<String,Object> fireflowVariableContext = new HashMap<String,Object>();
//			fireflowVariableContext.putAll(varValues);
//			fireflowVariableContext.putAll("<系统常量>");
			
//			ConditionResolver resolver = ctx.getEngineModule(ConditionResolver.class,BpelConstants.PROCESS_TYPE);
//			boolean b = resolver.resolveBooleanExpression(session, exp, fireflowVariableContext);
			Map<String, Object> fireflowVariableContext = ScriptEngineHelper
					.fulfillScriptContext(session, ctx,
							((WorkflowSessionLocalImpl)session).getCurrentProcessInstance(),
							((WorkflowSessionLocalImpl)session).getCurrentActivityInstance());

			boolean b = false;
			Object obj;

			obj = ScriptEngineHelper.evaluateExpression(ctx, exp,
					fireflowVariableContext);
			if (obj instanceof Boolean) {
				b = ((Boolean) obj).booleanValue();
			}
			if (b){
				this.executeChildActivity(session, token, child.getChildBpelActivity());
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.RUNNING);
				return result;
			}else{
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.COMPLETED);
				return result;
			}

		}
		

	}
	
	@Override
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		if (this.getChildren()==null || this.getChildren().size()==0){
			int level = getLevel();
			for (int i = 0; i < (level + 1); i++) {
				System.out.print("    ");// 打印空格
			}
			System.out.println(this.getName() + " completed!");
			return ContinueDirection.closeMe();
		}else{
			Child child = this.getChildren().get(0);
			Expression exp = child.getConditionExpression();
			
			RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
			
//			VariableService varService = ctx.getEngineModule(VariableService.class, token.getProcessType());
//			Map<String,Variable> vars = varService.getVariables(token.getProcessType(), token.getProcessInstanceId());
//			
//			Map<String,Object> varValues = new HashMap<String,Object>();
//			if (vars!=null){
//				Iterator<Entry<String,Variable>> it = vars.entrySet().iterator();
//				while(it.hasNext()){
//					Entry<String,Variable> entry = it.next();
//					varValues.put(entry.getKey(), entry.getValue().getVariableValue());
//				}
//			}
			
			//变量环境，包含流程变量和流程系统常量
//			Map<String,Object> fireflowVariableContext = new HashMap<String,Object>();
//			fireflowVariableContext.putAll(varValues);
//			fireflowVariableContext.putAll("<系统常量>");
			Map<String,Object> fireflowVariableContext = ScriptEngineHelper.fulfillScriptContext(session, ctx,
					((WorkflowSessionLocalImpl)session).getCurrentProcessInstance(),
					((WorkflowSessionLocalImpl)session).getCurrentActivityInstance());
	        
//			ConditionResolver resolver = ctx.getEngineModule(ConditionResolver.class,BpelConstants.PROCESS_TYPE);
//			boolean b = resolver.resolveBooleanExpression(session, exp, fireflowVariableContext);
			boolean b = false;

			Object obj;

			obj = ScriptEngineHelper.evaluateExpression(ctx, exp,
					fireflowVariableContext);
			if (obj instanceof Boolean) {
				b = ((Boolean) obj).booleanValue();
			}

			if (b){
				return ContinueDirection.runAgain();
			}else{
				int level = getLevel();
				for (int i = 0; i < (level + 1); i++) {
					System.out.print("    ");// 打印空格
				}
				System.out.println(this.getName() + " completed!");
				return ContinueDirection.closeMe();
			}

		}
				

	}
}
