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
package org.fireflow.engine.invocation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class DefaultAssignmentHandler extends AbsAssignmentHandler implements
		AssignmentHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 772130701903585306L;

	/**
	 * 解析参数 TODO 解析参数有必要吗？会不会把事情搞复杂，可以直接从processInstance中获取，用不着配置
	 * 
	 * @param parameterAssignment
	 * @return
	 */
	// protected Map<String, Object> resolveParameters(WorkflowSession session,
	// ProcessInstance processInstance, ActivityInstance activityInstance,
	// ResourceDef resource, List<Assignment> parameterAssignments)
	// throws ScriptException {
	// Map<String, Object> results = new HashMap<String, Object>();
	//
	// // 首先初始化results
	// List<Input> parameters = resource.getResolverParameters();
	// if (parameters != null) {
	// for (Input parameter : parameters) {
	// String strValue = parameter.getDefaultValueAsString();
	// if (strValue != null && !strValue.trim().equals("")) {
	// Object value;
	// try {
	// value =
	// JavaDataTypeConvertor.dataTypeConvert(parameter.getDataType(),strValue,
	// null);
	// } catch (ClassCastException e) {
	// throw new ScriptException(e);
	// } catch (ClassNotFoundException e) {
	// throw new ScriptException(e);
	// }
	// results.put(parameter.getName(), value);
	// } else {
	// results.put(parameter.getName(), null);
	// }
	// }
	// }
	//
	// if (parameterAssignments == null || parameterAssignments.size() == 0) {
	// return results;
	// }
	//
	// Map<String, Object> tmpResult = null;
	// Map<String, Object> contextVars =
	// ScriptEngineHelper.fulfillScriptContext(session,
	// runtimeContext, processInstance, activityInstance);
	// tmpResult = ScriptEngineHelper.resolveInputParameters(
	// runtimeContext, parameterAssignments, contextVars);
	//
	// results.putAll(tmpResult);
	//
	// return results;
	// }

	protected List<User> resolveResources(WorkflowSession session,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			List<ResourceDef> resourceRefs) {
		if (resourceRefs == null || resourceRefs.size() == 0) {
			return null;
		}
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(
				BeanFactory.class, activityInstance.getProcessType());

		List<User> users = new ArrayList<User>();

		for (ResourceDef resource : resourceRefs) {
			if (resource == null) {
				// TODO 记录警告日志，
				continue;
			}

			// try {
			ResourceResolver resourceResolver = null;
			String resolverBeanName = resource.getResolverBeanName();
			String resolverClassName = resource.getResolverClassName();
			// 首先应用resolver bean name
			if (!StringUtils.isEmpty(resolverBeanName)) {
				resourceResolver = (ResourceResolver) beanFactory
						.getBean(resolverBeanName);
			}
			else if (!StringUtils.isEmpty(resolverClassName)){
				// 然后应用resolver class name
				resourceResolver = (ResourceResolver) beanFactory
				.createBean(resolverClassName);
			}
			
			// 最后 ，通过resource type取得resolver
			if (resourceResolver == null) {
				resourceResolver = ResourceResolver
						.getResourceResolverForType(resource.getResourceType());
			}

			if (resourceResolver != null) {
				List<User> _users = resourceResolver.resolve(session,
						processInstance, activityInstance, resource);
				users.addAll(_users);
			}
		}
		return users;
	}

	@Override
	public List<User> resolvePotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		List<String> resourceIdList = resourceBinding.getPotentialOwnerRefs();
		List<ResourceDef> potentialOwnersResourceRef = new ArrayList<ResourceDef>();
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessLanguageManager processUtil = runtimeContext.getEngineModule(ProcessLanguageManager.class, processInstance.getProcessType());

		if (resourceIdList!=null && resourceIdList.size()>0){
			for (String id : resourceIdList){
				ResourceDef resourceDef = processUtil.getResourceDef(activityInstance, theActivity, id);
				potentialOwnersResourceRef.add(resourceDef);
			}
		}
		if (potentialOwnersResourceRef != null
				&& potentialOwnersResourceRef.size() > 0) {
			return this.resolveResources(session, processInstance,
					activityInstance, potentialOwnersResourceRef);
		} else {
			return null;
		}
	}

	@Override
	public List<User> resolveReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		List<String> resourceIdList = resourceBinding.getReaderRefs();
		List<ResourceDef> potentialOwnersResourceRef = new ArrayList<ResourceDef>();
		
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessLanguageManager processUtil = runtimeContext.getEngineModule(ProcessLanguageManager.class, processInstance.getProcessType());

		if (resourceIdList!=null && resourceIdList.size()>0){
			for (String id : resourceIdList){
				ResourceDef resourceDef = processUtil.getResourceDef(activityInstance, theActivity, id);
				potentialOwnersResourceRef.add(resourceDef);
			}
		}
		
		if (potentialOwnersResourceRef != null
				&& potentialOwnersResourceRef.size() > 0) {
			return this.resolveResources(session, processInstance,
					activityInstance, potentialOwnersResourceRef);
		} else {
			return null;
		}
	}

	@Override
	public List<User> resolveAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		List<String> administratorIdList = resourceBinding.getAdministratorRefs();
		List<ResourceDef> potentialOwnersResourceRef = new ArrayList<ResourceDef>();
		
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessLanguageManager processUtil = runtimeContext.getEngineModule(ProcessLanguageManager.class, processInstance.getProcessType());

		if (administratorIdList!=null && administratorIdList.size()>0){
			for (String id : administratorIdList){
				ResourceDef resourceDef = processUtil.getResourceDef(activityInstance, theActivity, id);
				potentialOwnersResourceRef.add(resourceDef);
			}
		}
		
		
		if (potentialOwnersResourceRef != null
				&& potentialOwnersResourceRef.size() > 0) {
			return this.resolveResources(session, processInstance,
					activityInstance, potentialOwnersResourceRef);
		} else {
			return null;
		}
	}
	
	public Map<WorkItemProperty,Object> resolveWorkItemPropertyValues(){
		return new HashMap<WorkItemProperty,Object>();
	}

	@Override
	public WorkItemAssignmentStrategy resolveAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity) {

		return resourceBinding.getAssignmentStrategy();
	}

}
