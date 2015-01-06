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
package org.fireflow.demo.workflow;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.demo.hr.bean.Organization;
import org.fireflow.demo.misc.BeanUtilEx;
import org.fireflow.demo.workflow.ext.ProcessInstanceEx;
import org.fireflow.demo.workflow.ext.WorkItemExt;
import org.fireflow.demo.workflow.ext.WorkflowUser;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.DepartmentImpl;
import org.fireflow.engine.modules.ousystem.impl.GroupImpl;
import org.fireflow.engine.modules.ousystem.impl.RoleImpl;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class WorkflowUtil {
	private static Map<String,String> WORKITEM_STATE_DISPLAY_NAME = new HashMap<String,String>();
	static{
		WORKITEM_STATE_DISPLAY_NAME.put("INITIALIZED", "待签收");
		WORKITEM_STATE_DISPLAY_NAME.put("RUNNING", "待处理");
		WORKITEM_STATE_DISPLAY_NAME.put("FAULTING", "异常");
		WORKITEM_STATE_DISPLAY_NAME.put("FAULTED", "异常结束");
		
		WORKITEM_STATE_DISPLAY_NAME.put("ABORTING", "中止");
		WORKITEM_STATE_DISPLAY_NAME.put("ABORTED", "已中止");
		
		WORKITEM_STATE_DISPLAY_NAME.put("COMPLETED", "已处理");
		WORKITEM_STATE_DISPLAY_NAME.put("DISCLAIMED", "已退签收");
		WORKITEM_STATE_DISPLAY_NAME.put("REJECTED", "已退回");
		
		WORKITEM_STATE_DISPLAY_NAME.put("REASSIGNED", "已转交他人");
		WORKITEM_STATE_DISPLAY_NAME.put("CANCELLED", "已撤销");
		
		WORKITEM_STATE_DISPLAY_NAME.put("READONLY", "抄阅项");
	}
	
	private static Map<String,String> PROCESSINSTANCE_STATE_DISPLAY_NAME = new HashMap<String,String>();
	static{
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("INITIALIZED", "已就绪");
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("RUNNING", "运行中");
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("FAULTING", "异常");
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("FAULTED", "异常结束");
		
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("ABORTING", "中止");
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("ABORTED", "已中止");
		
		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("COMPLETED", "已处理");


		PROCESSINSTANCE_STATE_DISPLAY_NAME.put("CANCELLED", "已撤销");
		
	}	
	
	/**
	 * 从业务系统当前用户转换为工作流用户。<br/>
	 * 有两种方案可以实现该需求：1、业务系统中的用户对象（如DemoUser）实现org.fireflow.engine.modules.
	 * ousystem.User<br/>
	 * 2、将业务系统中的用户对象组装成org.fireflow.engine.modules.ousystem.impl.UserImpl返回，如本函数
	 * 。
	 * 
	 * @param session
	 * @return
	 */
	public static User getCurrentWorkflowUser() {
		// 从Session中获得当前用户
		org.fireflow.demo.security.bean.User demoUser = (org.fireflow.demo.security.bean.User) SecurityUtils
				.getSubject().getPrincipal();
		User fireUser = convertAppUser2FireflowUser(demoUser);
		// userImpl.setProperties(properties);//可以设置更多属性
		return fireUser;
	}

	public static User convertAppUser2FireflowUser(
			org.fireflow.demo.security.bean.User demoUser) {
		if (demoUser == null)
			return null;
		WorkflowUser fireUser = new WorkflowUser();
		fireUser.setId(demoUser.getLoginName());
		fireUser.setName(demoUser.getName());
		fireUser.setDeptId(demoUser.getGroupCode());//groupCode 等价与流程系统中的departmentId
		fireUser.setDeptName(demoUser.getGroupName());
		
		fireUser.setGroupCode(demoUser.getGroupCode());
		fireUser.setGroupName(demoUser.getGroupName());

		return fireUser;
	}

	public static Department convertAppDept2FireflowDept(Organization demoDept) {
		if (demoDept == null)
			return null;
		DepartmentImpl dept = new DepartmentImpl();
		dept.setId(demoDept.getCode());
		dept.setName(demoDept.getName());
		return dept;
	}
	
	public static Group convertAppGroup2FireflowGroup(org.fireflow.demo.security.bean.Group appGroup){
		GroupImpl group = new GroupImpl();
		group.setId(appGroup.getCode());
		group.setName(appGroup.getName());
		group.setDeptId(appGroup.getOrgCode());
		group.setDeptName(appGroup.getOrgName());
		
		return group;
	}
	
	public static Role convertAppRole2FireflowRole(org.fireflow.demo.security.bean.Role appRole){
		RoleImpl group = new RoleImpl();
		group.setId(appRole.getCode());
		group.setName(appRole.getName());
		
		return group;
	}

	public static Activity getThisActivity(WorkflowSession session,
			LocalWorkItem currentWorkItem) throws InvalidModelException {
		WorkflowStatement stmt = session.createWorkflowStatement();
		ProcessKey pk = new ProcessKey(currentWorkItem.getProcessId(),
				currentWorkItem.getVersion(), currentWorkItem.getProcessType());

		WorkflowProcess process = null;

		process = (WorkflowProcess) stmt.getWorkflowProcess(pk);
		Activity activity = (Activity)process.findWorkflowElementById(currentWorkItem.getActivityId());

		return activity;

	}
	
	public static List<Activity> getNextActivities(WorkflowSession session,
			LocalWorkItem currentWorkItem) throws InvalidModelException {
		WorkflowStatement stmt = session.createWorkflowStatement();
		ProcessKey pk = new ProcessKey(currentWorkItem.getProcessId(),
				currentWorkItem.getVersion(), currentWorkItem.getProcessType());

		WorkflowProcess process = null;

		process = (WorkflowProcess) stmt.getWorkflowProcess(pk);

		List<Activity> result = process.findNextActivities(currentWorkItem
				.getActivityId());

		
		return result;

	}
	

	
	public static List<WorkItemExt> workItemListToWorkItemExtList(List<WorkItem> list){
		List<WorkItemExt> list2 = new ArrayList<WorkItemExt>();
		
		if (list!=null){
			for (WorkItem wi :list){
				
				
				list2.add(workItemToWorkItemExt(wi));
			}
		}
		
		return list2;
	}
	
	public static WorkItemExt workItemToWorkItemExt(WorkItem wi){
		WorkItemExt ext = new WorkItemExt();

		try {
			BeanUtilEx.copyProperties(ext, wi);
			ext.setStateValue(wi.getState().getValue());
			ext.setStateDisplayName(WORKITEM_STATE_DISPLAY_NAME.get(wi.getState().name()));
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ext;
	}
	
	
	public static List<ProcessInstanceEx> processInstanceListToProcessInstanceExtList(List<ProcessInstance> list,Map<String,String> actInstInfo){
		List<ProcessInstanceEx> list2 = new ArrayList<ProcessInstanceEx>();
		
		if (list!=null){
			for (ProcessInstance wi :list){
				
				
				list2.add(processInstanceToProcessInstanceEx(wi,actInstInfo));
			}
		}
		
		return list2;
	}
	
	
	public static ProcessInstanceEx processInstanceToProcessInstanceEx(ProcessInstance procInst,Map<String,String> actInstInfo){
		ProcessInstanceEx procInstEx = new ProcessInstanceEx();
		try {
			BeanUtilEx.copyProperties(procInstEx, procInst);
			procInstEx.setStateValue(procInst.getState().getValue());
			procInstEx.setStateDisplayName(PROCESSINSTANCE_STATE_DISPLAY_NAME.get(procInst.getState().name()));
			procInstEx.setCurrentActivityInstances(actInstInfo.get(procInst.getId()));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return procInstEx;
	}
}
