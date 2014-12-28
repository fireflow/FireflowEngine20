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
package org.fireflow.service.email.send;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Input;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.email.MailTemplate;
import org.firesoa.common.util.JavaDataTypeConvertor;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailSenderInvoker extends AbsServiceInvoker {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.AbsServiceInvoker#getServiceObject(org.fireflow.engine.context.RuntimeContext, org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding)
	 */
	@Override
	protected Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef serviceDef,Object activity) throws ServiceInvocationException {
		MailSenderImpl sender = new MailSenderImpl();
		sender.setMailSentServiceDef((MailSendServiceDef)serviceDef);
		CalendarService theCalendar = runtimeContext.getDefaultEngineModule(CalendarService.class);
		if (theCalendar!=null){
			sender.setSentDate(theCalendar.getSysDate());
		}
		return sender;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName, Object[] params) {
		Class[] parameterTypes = new Class[]{String.class,String.class,String.class,String.class,Boolean.class};
		return parameterTypes;
		
		//本服务中，根据参数数量即可判断参数类型
		/* 2012-03-11 单个参数的接口方法没有存在的必要
		if (params!=null && params.length==6){
			Class[] parameterTypes = new Class[]{String.class,List.class,List.class,String.class,String.class,Boolean.class};
			return parameterTypes;
		}else if (params!=null && params.length==1){
			Class[] parameterTypes = new Class[]{MailMessage.class};
			return parameterTypes;
		}
		return null;
		*/
		
	}
	
	protected Object[] resolveInputParams(RuntimeContext runtimeContext,
			WorkflowSession session,ProcessInstance processInstance, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef service)throws ScriptException {
		Map<String,Object> inputParamValues = resolveInputAssignments(runtimeContext,session,processInstance,
				activityInstance,serviceBinding,service); 
		
		MailSendServiceDef serviceDef = (MailSendServiceDef)service;
		MailTemplate mailTemplate = serviceDef.getMailTemplate();
		
//		OperationDef operation = serviceBinding.getOperation();
//		List<Input> inputs = operation.getInputs();
		List<Assignment> inputAssignmentList = serviceBinding.getInputAssignments();
		Map<String, Object> contextVars = null;

		List<Object> args = new ArrayList<Object>();
		for (Assignment assignment : inputAssignmentList) {
			Expression toExpression = assignment.getTo();
			if (inputParamValues!=null && inputParamValues.containsKey(toExpression.getName())){
				Object paramValue = inputParamValues.get(toExpression.getName());
				args.add(paramValue);
			}else{
				//如果没有绑定，则从MailTemplate中获取
				if (mailTemplate!=null ){
					Expression exp = mailTemplate.getMailField(toExpression.getName());
					if (contextVars==null){
						contextVars = ScriptEngineHelper.fulfillScriptContext(session,
								runtimeContext, processInstance, activityInstance);
					}
					Object obj = ScriptEngineHelper.evaluateExpression(runtimeContext, exp, contextVars);
					try{
						args.add(JavaDataTypeConvertor.dataTypeConvert(toExpression.getDataType(),obj,null));
					}catch(Exception e){
						args.add(null);
					}
					
				}else{
					args.add(null);
				}
				
			}

		}
		return args.toArray();
	}
	
}
