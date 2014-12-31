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
package org.fireflow.pdl.fpdl.enginemodules.workitem;

import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.engine.modules.workitem.event.WorkItemEventTrigger;
import org.fireflow.engine.modules.workitem.impl.AbsWorkItemManager;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.service.human.HumanService;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class WorkItemManagerFpdl20Impl extends AbsWorkItemManager {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.service.form.WorkItemManager#createWorkItem(org.fireflow
	 * .engine.WorkflowSession,
	 * org.fireflow.engine.entity.runtime.ProcessInstance,
	 * org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.String)
	 */
	public WorkItem createWorkItem(WorkflowSession currentSession,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			User user, Object theActivity,
			Map<WorkItemProperty, Object> workitemPropertyValues)
			throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) currentSession)
		.getRuntimeContext();
		
		Activity activity = (Activity) theActivity;
		ServiceBinding serviceBinding = activity.getServiceBinding();
		HumanService humanService = null;
		if (serviceBinding != null) {
			ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class, activityInstance.getProcessType());
			humanService = (HumanService) processUtil.getServiceDef(activityInstance, activity, serviceBinding.getServiceId());
		}

		ResourceBinding resourceBinding = activity.getResourceBinding();


		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, activityInstance.getProcessType());
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, activityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		LocalWorkItemImpl wi = new LocalWorkItemImpl();

		wi.setWorkItemName(activityInstance.getDisplayName());
		// 计算工作项摘要表达式
		if (humanService != null) {
			wi.setSubject(parseDescription(humanService.getWorkItemSubject(),
					currentSession, ctx, processInstance, activityInstance));

			
		}
		wi.setCreatedTime(calendarService.getSysDate());
		wi.setExpiredTime(activityInstance.getExpiredTime());
		
		//工作项所有者信息
		wi.setOwnerId(user.getId());
		wi.setOwnerName(user.getName());
		wi.setOwnerDeptId(user.getDeptId());
		wi.setOwnerDeptName(user.getDeptName());
		
		//构建表单Url 
		String formUrl = humanService.getFormUrl();//将参数追加在formUrl后面
		if(formUrl!=null){
			Map<String, Object> theInputValues = null;
			try {
				theInputValues = AbsServiceInvoker.resolveInputAssignments(ctx,currentSession,
						processInstance,activityInstance,activity.getServiceBinding(),humanService);
			} catch (ScriptException e) {
				throw new EngineException(activityInstance,"");
			}
			StringBuffer queryStr = new StringBuffer("");
			int flag = 0;
			if (theInputValues!=null && theInputValues.size()>0){
				Iterator<Map.Entry<String, Object>> iterator = theInputValues.entrySet().iterator();
				while (iterator.hasNext()){
					flag = flag+1;
					Map.Entry<String, Object> entry = iterator.next();
					queryStr.append(entry.getKey())
						.append("=")
						.append(entry.getValue());
					if (flag>0 && flag<theInputValues.size()){
						queryStr.append("&");
					}
				}
			}
			if (queryStr.length()>0){
				int idx = formUrl.indexOf("?");
				if (idx>=0){
					formUrl = formUrl + "&" +queryStr.toString();
				}else{
					formUrl = formUrl + "?"+queryStr.toString();
				}
			}
			
			//TODO 是否要将WorkItemId固定追加在url末尾？
			//注：此处无法将workItemId追加在url末尾，因为此时workItem还未保存，id属性还为空
		}
		wi.setActionUrl(formUrl);
		wi.setBizId(activityInstance.getBizId());
		
		if (resourceBinding != null
				&& resourceBinding.getAssignmentStrategy() != null) {
			wi.setAssignmentStrategy(resourceBinding.getAssignmentStrategy());
		}

		wi.setResponsiblePersonId(user.getId());
		wi.setResponsiblePersonName(user.getName());
		wi.setResponsiblePersonDeptId(user.getDeptId());
		wi.setResponsiblePersonDeptName(user.getDeptName());


		if (workitemPropertyValues != null) {
			if (workitemPropertyValues.get(WorkItemProperty.STATE) != null) {
				wi.setState((WorkItemState) workitemPropertyValues
						.get(WorkItemProperty.STATE));
			}
			if (workitemPropertyValues
					.get(WorkItemProperty.ASSIGNMENT_STRATEGY) != null) {
				wi.setAssignmentStrategy((WorkItemAssignmentStrategy) workitemPropertyValues
						.get(WorkItemProperty.ASSIGNMENT_STRATEGY));
			}

			wi.setReassignType((String) workitemPropertyValues
					.get(WorkItemProperty.REASSIGN_TYPE));
			wi.setParentWorkItemId((String) workitemPropertyValues
					.get(WorkItemProperty.PARENT_WORKITEM_ID));
		}

		
		wi.setProcInstCreatorId(processInstance.getCreatorId());
		wi.setProcInstCreatorName(processInstance.getCreatorName());
		wi.setProcInstCreatedTime(processInstance.getCreatedTime());
		wi.setProcessId(processInstance.getProcessId());
		wi.setSubProcessId(processInstance.getSubProcessId());
		wi.setProcessType(processInstance.getProcessType());
		wi.setVersion(processInstance.getVersion());
		wi.setActivityId(activityInstance.getNodeId());
		wi.setProcessInstanceId(processInstance.getId());
		wi.setStepNumber(activityInstance.getStepNumber());
		
		wi.setActivityInstanceId(activityInstance.getId());
		
		// 发布事件
		this.fireWorkItemEvent(currentSession, wi, theActivity,
				WorkItemEventTrigger.BEFORE_WORKITEM_CREATED);
		
		workItemPersister.saveOrUpdate(wi);

		// 发布事件
		this.fireWorkItemEvent(currentSession, wi, theActivity,
				WorkItemEventTrigger.AFTER_WORKITEM_CREATED);
		return wi;
	}

	private String parseDescription(Expression descExpression,
			WorkflowSession session, RuntimeContext runtimeContext,
			ProcessInstance processInstance, ActivityInstance activityInstance) {
		if (descExpression == null
				|| StringUtils.isEmpty(descExpression.getLanguage())) {
			return "";
		}
		Map<String, Object> contextVars = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		Object obj = null;

		obj = ScriptEngineHelper.evaluateExpression(runtimeContext,
				descExpression, contextVars);

		return obj == null ? null : obj.toString();

	}
}
