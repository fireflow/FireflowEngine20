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

import java.util.List;
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
import org.fireflow.pvm.pdllogic.ExecuteResult;

/**
 * @author 非也
 * @version 2.0
 */
public class If extends StructureActivity {
	Child elseActivity  = null;
	
	public If(String name,String condition,BpelActivity bpelActivity){
		super(name);
		bpelActivity.setParent(this);
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody(condition);
		Child childActivity = new Child(exp,bpelActivity);
		this.addChild(childActivity);
	}
	
	public If addElseIf(String condition,BpelActivity bpelActivity){
		bpelActivity.setParent(this);
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody(condition);
		if (elseActivity==null){
			this.addChild(new Child(exp,bpelActivity));
		}else{
			int index = this.getChildren().size()-2;
			this.addChild(index, new Child(exp,bpelActivity));
		}
		return this;
	}
	
	public If setElse(BpelActivity bpelActivity){
		bpelActivity.setParent(this);
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage("JEXL");
		exp.setBody("");
		Child child = new Child(exp,bpelActivity);
		if (this.elseActivity==null){
			this.elseActivity = child;
			this.addChild(child);
		}else{
			this.elseActivity = child;
			int index = this.getChildren().size()-1;
			this.setChild(index, child);
		}
		return this;
	}




	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
//		VariableService varService = runtimeContext.getEngineModule(VariableService.class, token.getProcessType());
//		Map<String,Variable> vars = varService.getVariables(token.getProcessType(), token.getProcessInstanceId());
//		
//		Map<String,Object> varValues = new HashMap<String,Object>();
//		if (vars!=null){
//			Iterator<Entry<String,Variable>> it = vars.entrySet().iterator();
//			while(it.hasNext()){
//				Entry<String,Variable> entry = it.next();
//				varValues.put(entry.getKey(), entry.getValue().getVariableValue());
//			}
//		}
		
		//变量环境，包含流程变量和流程系统常量
//		Map<String,Object> fireflowVariableContext = new HashMap<String,Object>();
//		fireflowVariableContext.putAll(varValues);
//		fireflowVariableContext.putAll("<系统常量>");		
		Map<String,Object> fireflowVariableContext = ScriptEngineHelper.fulfillScriptContext(session, runtimeContext,
				((WorkflowSessionLocalImpl)session).getCurrentProcessInstance(),
				((WorkflowSessionLocalImpl)session).getCurrentActivityInstance());

        
		List<Child> theChildren = this.getChildren();
		for(Child child :theChildren){
			Expression exp = child.getConditionExpression();
			if (exp==null || exp.getBody()==null || exp.getBody().trim().equals("")){
				this.executeChildActivity(session,token,child.getChildBpelActivity());
				break;
			} else {
				boolean b = true;

				Object obj = ScriptEngineHelper.evaluateExpression(
						runtimeContext, exp, fireflowVariableContext);
				if (obj instanceof Boolean) {
					b = ((Boolean) obj).booleanValue();
				}

				if (b) {
					this.executeChildActivity(session, token,
							child.getChildBpelActivity());
					break;
				}
			}
			
		}

		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}


}
