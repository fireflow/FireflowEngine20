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
package org.fireflow.web_client.app.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.script.ScriptException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.demo.fireflow_ext.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.service.human.HumanService;
import org.fireflow.web_client.util.Constants;
import org.fireflow.web_client.util.Utils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * 根据Workitem的信息打开表单，如果WorkItemId为空，表示创建流程实例。
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 *
 */
public class WorkflowOperationServlet extends HttpServlet {
	protected WebApplicationContext  springCtx  = null;
	protected RuntimeContext fireContext  = null;
	protected TransactionTemplate tramsactionTemplate  = null;
    public void init() throws ServletException {
		//准备相关参数及相关spring bean
    	springCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	fireContext = (RuntimeContext)springCtx.getBean(RuntimeContext.Fireflow_Runtime_Context_Name);
    	tramsactionTemplate = (TransactionTemplate)springCtx.getBean("demoTransactionTemplate");
    	
    }
    
	
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
    	this.doPost(req, resp);
    }
    
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String actionType = req.getParameter(Constants.ACTION_TYPE);
		if (Constants.CREATE_PROCESS_INSTANCE.equals(actionType)){
			_createProcessInstance(req,resp);
		}
		else if (Constants.COMPLETE_WORKITEM.equals(actionType)){
			_completeWorkItem(req,resp);
		}
		else if (Constants.LIST_WORKITEMS_IN_PROCESS_INSTANCE.equals(actionType)){
			_listWorkItemsInProcessInstance(req,resp);
		}
		else if (Constants.LIST_MY_ACTIVE_PROCESS_INSTANCE.equals(actionType)){
			_listMyActiveProcessInstance(req,resp);
		}
		else if (Constants.LIST_TODO_WORKITEMS.equals(actionType)){
			this._listMyTodoWorkItems(req, resp);
		}
		else if (Constants.LIST_HAVEDONE_WORKITEMS.equals(actionType)){
			this._listMyHaveDoneWorkItems(req, resp);
		}
		else if (Constants.LIST_READONLY_WORKITEMS.equals(actionType)){
			this._listReadOnlyWorkItems(req, resp);
		}
		else if (Constants.CLAIM_WORKITEM.equals(actionType)){
			this._claimWorkItem(req,resp);
		}
		else if (Constants.DISCLAIM_WORKITEM.equals(actionType)){
			this._disclaimWorkItem(req,resp);
		}
		else if (Constants.OPEN_BIZ_FORM.equals(actionType)){
			this._openBizForm(req,resp);
		}
		else if (Constants.OPEN_NEXT_STEP_ACTOR_SELECTOR.equals(actionType)){
			this._openNextStepActorSelector(req,resp);
		}
		else if (Constants.LOAD_OU_AS_JSTREE_XML.equals(actionType)){
			_loadOUAsJstreeXml(req,resp);
		}
		else if (Constants.OPEN_REASSIGN_ACTOR_SELECTOR.equals(actionType)){
			_openReassignActorSelector(req,resp);
		}
		else if (Constants.REASSIGN_WORKITEM.equals(actionType)){
			_reassignWorkItem(req,resp);
		}
		else if (Constants.OPEN_TARGET_ACTIVITY_SELECTOR.equals(actionType)){
			_openTargetActivitySelector(req,resp);
		}
		else if (Constants.COMPLETE_WORKITEM_AND_JUMP_TO.equals(actionType)){
			this._completeWorkItem(req, resp);
		}
	}
	
	/**
	 * 为下一个步骤选择操作者
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _openTargetActivitySelector(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String workItemId = req.getParameter(Constants.WORKITEM_ID);
		
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);

		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem wi = workItemQuery.get(workItemId);
		if (wi==null){
			forwardToErrorPage(req, resp, "没有找到工作项，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}
		
		if (!(wi instanceof LocalWorkItemImpl)){
			forwardToErrorPage(req, resp, "不可以对Remote WorkItem执行签收操作，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}
		
		//1、查找到所有的Activity
		//TODO 可以从一个子流程跳转到另外一个子流程吗？
		
		LocalWorkItem workItem = (LocalWorkItem)wi;
		ActivityInstance activityInstance = workItem.getActivityInstance();
		WorkflowProcess workflowProcess = null;
		try {
			workflowProcess = (WorkflowProcess)activityInstance.getWorkflowProcess(workflowSession);
		} catch (InvalidModelException e1) {
			forwardToErrorPage(req, resp, "查找工作项对应的流程失败[workItemId=" + workItemId
					+ "]失败。", e1);
			return;
		}
		
		List<Activity> allActivities = new ArrayList<Activity>();
		List<SubProcess> allLocalSubProcesses = workflowProcess.getLocalSubProcesses();
			
		for (SubProcess subProcess:allLocalSubProcesses){
			allActivities.addAll(subProcess.getActivities());
		}

		
		Activity activity = (Activity)workflowProcess.findWorkflowElementById(activityInstance.getNodeId());

		req.setAttribute("allActivities", allActivities);
		req.setAttribute("thisActivity", activity);
		
		//3、跳转到表单页面，让用户补充业务信息
		String url = "/fireflow_client/_select_next_activity.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(url);
		dispatcher.forward(req, resp);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void _reassignWorkItem(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		final String workItemId = req.getParameter(Constants.WORKITEM_ID);
		String reassignType = req.getParameter("reassign_flag");

		final Map<String,AssignmentHandler> reassignmentHandlers = createReassignmentHandler(req);

		
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		try{
			WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
			WorkItem wi = workItemQuery.get(workItemId);
			
			if (wi==null){
				forwardToErrorPage(req, resp, "没有找到工作项，workItemId=[" + workItemId
						+ "]。", null);
				return;
			}
			
			if (!(wi instanceof LocalWorkItemImpl)){
				forwardToErrorPage(req, resp, "不可以对Remote WorkItem执行委派操作，workItemId=[" + workItemId
						+ "]。", null);
				return;
			}

			LocalWorkItem workItem = (LocalWorkItem)wi;
			
			ActivityInstance activityInstance = workItem.getActivityInstance();
			
			final ReassignmentHandler reassignmentHandler = (ReassignmentHandler)reassignmentHandlers.get(activityInstance.getNodeId());
			if ("beforeme".equals(reassignType)){
				reassignmentHandler.setReassignType(WorkItem.REASSIGN_BEFORE_ME);
			}else {
				reassignmentHandler.setReassignType(WorkItem.REASSIGN_AFTER_ME);
			}
			reassignmentHandler.setParentWorkItemId(workItemId);
			
			tramsactionTemplate.execute(new TransactionCallback() {

				public Object doInTransaction(TransactionStatus status) {
					WorkflowStatement workflowStatement = workflowSession.createWorkflowStatement();
					try {
						workflowStatement.reassignWorkItemTo(workItemId, reassignmentHandler, null,null,null);
					} catch (InvalidOperationException e) {
						throw new RuntimeException(e);
					}
					return null;
				}
			});
		}catch (Exception e) {
			forwardToErrorPage(req, resp, "提交流程失败，当前工作项是[workItemId=" + workItemId
					+ "]。", e);
			return;
		}

		
		//3、导航到指定页面，缺省为我的待办页面
		String forwardURL = req.getParameter(Constants.FORWARD_URL);
		if (forwardURL==null || forwardURL.trim().equals("")){
			forwardURL = "/servlet/WorkflowOperationServlet?"+Constants.ACTION_TYPE+"="+Constants.LIST_TODO_WORKITEMS;
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardURL);
		dispatcher.forward(req, resp);
	}
	
	protected void _openReassignActorSelector(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String workItemId = req.getParameter(Constants.WORKITEM_ID);
		String reassignFlag = req.getParameter("reassignFlag");
//		System.out.println("=========reassignFlag is "+reassignFlag);
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);

		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem wi = workItemQuery.get(workItemId);
		
		if (wi==null){
			forwardToErrorPage(req, resp, "没有找到工作项，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}
		
		if (!(wi instanceof LocalWorkItemImpl)){
			forwardToErrorPage(req, resp, "不可以对Remote WorkItem执行委派操作，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}

		LocalWorkItem workItem = (LocalWorkItem)wi;
		
		ActivityInstance activityInstance = workItem.getActivityInstance();
		
		req.setAttribute("reassignFlag", reassignFlag);
		req.setAttribute("workItem", workItem);
		req.setAttribute("activityInstance", activityInstance);
		
		
		String url = "/fireflow_client/_reassign_actor_selector.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(url);
		dispatcher.forward(req, resp);
	}
	
	protected void _loadOUAsJstreeXml(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String current_node_id = req.getParameter("current_node_id");
		
		StringBuffer sbuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>");
		
		OUSystemConnector ouConnector = fireContext.getEngineModule(OUSystemConnector.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		if ("-1".equals(current_node_id)){
			List<Department> allTopDeptList = ouConnector.findAllTopDepartments();//查找到所有顶层组织机构，一般情况下只有一条记录
			for (Department dept : allTopDeptList){
				sbuf.append("<item parent_id=\"").append("0\"")
					.append(" id=\"").append(dept.getId()).append("\"")
					.append(" state=\"closed\"")
					.append(" node_type=\"dept\"")
					.append(">\n");
				
				sbuf.append("\t<content>\n");
				sbuf.append("\t\t<name><![CDATA[").append(dept.getName()).append("]]></name>\n");
				sbuf.append("\t</content>\n");
				
				sbuf.append("</item>");
			}
		}else{
			//下级部门
			List<Department> allChildDeptList =  ouConnector.findChildDepartments(current_node_id);
			for (Department dept : allChildDeptList){
				sbuf.append("<item parent_id=\"").append(current_node_id).append("\"")
					.append(" id=\"").append(dept.getId()).append("\"")
					.append(" state=\"closed\"")
					.append(" node_type=\"dept\"")
					.append(">\n");
				
				sbuf.append("\t<content>\n");
				sbuf.append("\t\t<name><![CDATA[").append(dept.getName()).append("]]></name>\n");
				sbuf.append("\t</content>\n");
				
				sbuf.append("</item>");
			}
			
			//本部门人员
			List<User> allUsers = ouConnector.findUsersInDepartment(current_node_id);
		
			for (User user : allUsers){
				sbuf.append("<item parent_id=\"").append(current_node_id).append("\"")
					.append(" id=\"").append(user.getId()).append("\"")
					.append(" state=\"closed\"")
					.append(" node_type=\"user\"")
					.append(" name=\"").append(user.getName()).append("\"")
					.append(" dept_name=\"").append(user.getDeptName()).append("\"")
					.append(" dept_id=\"").append(user.getDeptId()).append("\"")
					.append(">\n");
				System.out.println("user.getDeptName() is "+user.getDeptName());
				sbuf.append("\t<content>\n");
				sbuf.append("\t\t<name><![CDATA[").append(user.getName()).append("]]></name>\n");
				sbuf.append("\t</content>\n");
				
				sbuf.append("</item>");
			}
		}
		
		sbuf.append("\n</root>");
		String xml = sbuf.toString();
		System.out.println(xml);
		
		resp.setContentType("text/xml");
		Writer writer = resp.getWriter();
		writer.write(xml);
	}
	
	/**
	 * 为下一个步骤选择操作者
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _openNextStepActorSelector(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		String workItemId = req.getParameter(Constants.WORKITEM_ID);
		
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);

		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem wi = workItemQuery.get(workItemId);

		if (wi==null){
			forwardToErrorPage(req, resp, "没有找到工作项，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}
		
		if (!(wi instanceof LocalWorkItemImpl)){
			forwardToErrorPage(req, resp, "不可以对Remote WorkItem执行该操作，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}

		LocalWorkItem workItem = (LocalWorkItem)wi;
		
		//1、找到当前Activity的后继Activity
		ActivityInstance activityInstance = workItem.getActivityInstance();
		WorkflowProcess workflowProcess = null;
		try {
			workflowProcess = (WorkflowProcess)activityInstance.getWorkflowProcess(workflowSession);
		} catch (InvalidModelException e1) {
			forwardToErrorPage(req, resp, "查找工作项对应的流程失败[workItemId=" + workItemId
					+ "]失败。", e1);
			return;
		}
		Activity activity = (Activity)workflowProcess.findWorkflowElementById(activityInstance.getNodeId());
		
		if (activity==null){
			forwardToErrorPage(req,resp,"流程[processId="+activityInstance.getProcessId()+"]中没有id为"+activityInstance.getNodeId()+"的Activity。",null);
			return ;
		}

		
		//
		List<Activity> nextActivities = activity.getNextActivities();
		req.setAttribute("nextActivities", nextActivities);
		req.setAttribute("thisActivity", activity);
		
		//2、查询组织机构树
		OUSystemConnector ouSystemConnnector = fireContext.getEngineModule(OUSystemConnector.class, activityInstance.getProcessType());
//		ouSystemConnnector.
		
		
		//3、跳转到表单页面，让用户补充业务信息
		String url = "/fireflow_client/_next_step_and_actors.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(url);
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 打开业务表单
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _openBizForm(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		
		//1、获得表单URL
		String workItemId = req.getParameter(Constants.WORKITEM_ID);
		
		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem wi = workItemQuery.get(workItemId);
		

		
		if (wi==null){
			forwardToErrorPage(req, resp, "没有找到工作项，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}
		
		if (!(wi instanceof LocalWorkItemImpl)){
			forwardToErrorPage(req, resp, "不可以对Remote WorkItem执行该操作，workItemId=[" + workItemId
					+ "]。", null);
			return;
		}

		LocalWorkItem workItem = (LocalWorkItem)wi;
		String formURL = workItem.getActionUrl();
		//2、获得表单的输入参数，并将参数设置到request attribute
		ActivityInstance activityInstance = workItem.getActivityInstance();
		WorkflowProcess workflowProcess = null;
		try {
			workflowProcess = (WorkflowProcess)activityInstance.getWorkflowProcess(workflowSession);
		} catch (InvalidModelException e1) {
			forwardToErrorPage(req, resp, "打开工作项的表单失败[workItemId=" + workItemId
					+ "]失败。", e1);
			return;
		}
		Activity activity = (Activity)workflowProcess.findWorkflowElementById(activityInstance.getNodeId());
		
		if (activity==null){
			forwardToErrorPage(req,resp,"流程[processId="+activityInstance.getProcessId()+"]中没有id为"+activityInstance.getNodeId()+"的Activity。",null);
			return ;
		}
		if (activity.getServiceBinding()==null || activity.getServiceBinding().getServiceId()==null){
			forwardToErrorPage(req,resp,"活动[activityId="+activity.getId()+",displayName="+activity.getDisplayName()+"]中没有邦定服务。",null);
			return ;
		}
		final ServiceDef serviceDef = workflowProcess.getService(activity.getServiceBinding().getServiceId());
		if (serviceDef==null || !(serviceDef instanceof HumanService)){
			forwardToErrorPage(req,resp,"流程[processId="+activityInstance.getProcessId()+"]中没有找到id="+activity.getServiceBinding().getServiceId()+"的服务；或者该服务不是人工任务。",null);
			return ;
		}
		
		
		ProcessInstance processInstance = activityInstance.getProcessInstance(workflowSession);
		Map<String, Object> theInputValues = null;
		try {
			theInputValues = AbsServiceInvoker.resolveInputAssignments(fireContext,workflowSession,processInstance,activityInstance,activity.getServiceBinding(),serviceDef);
		} catch (ScriptException e) {
			forwardToErrorPage(req, resp, "打开工作项的表单失败[workItemId=" + workItemId
					+ "]失败。", e);
			return;
		}
		if (theInputValues.size()>0){
			Iterator<Entry<String,Object>> iterator = theInputValues.entrySet().iterator();
			while (iterator.hasNext()){
				Entry<String,Object> entry = iterator.next();
				req.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		
		
		
		//3、跳转到表单页面，让用户补充业务信息
		RequestDispatcher dispatcher = req.getRequestDispatcher(formURL);
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 退签收工作项
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void _disclaimWorkItem(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		final String workItemId = req.getParameter(Constants.WORKITEM_ID);

		//备注信息

		String note = req.getParameter(WorkItemProperty.NOTE.getPropertyName());
		if (note==null || note.trim().equals("")){
			note = (String)req.getAttribute(WorkItemProperty.NOTE.getPropertyName());
		}

		if (note==null){
			note = "退签收。";
		}
		final String __note = note;
		
		//1、获得当前系统登录用户并创建Session；系统登录用户必须实现org.fireflow.engine.modules.ousystem.User
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		
		//2、提交流程
		try {
			tramsactionTemplate.execute(new TransactionCallback() {

				public Object doInTransaction(TransactionStatus status) {
					WorkflowStatement stmt = workflowSession
							.createWorkflowStatement();
					try {
						WorkItem result = stmt.disclaimWorkItem(workItemId,null,null,__note);
						return result;
					} catch (InvalidOperationException e) {
						throw new RuntimeException(e);
					}
				}

			});
		} catch (Exception e) {
			forwardToErrorPage(req, resp, "提交流程失败，当前工作项是[workItemId=" + workItemId
					+ "]。", e);
			return;
		}
		
		//3、导航到指定页面，缺省为我的待办页面
		String forwardURL = req.getParameter(Constants.FORWARD_URL);
		if (forwardURL==null || forwardURL.trim().equals("")){
			forwardURL = "/servlet/WorkflowOperationServlet?"+Constants.ACTION_TYPE+"="+Constants.LIST_TODO_WORKITEMS;
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardURL);
		dispatcher.forward(req, resp);		
	}
	
	@SuppressWarnings("unchecked")
	protected void _claimWorkItem(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		final String workItemId = req.getParameter(Constants.WORKITEM_ID);

		//1、获得当前系统登录用户并创建Session；系统登录用户必须实现org.fireflow.engine.modules.ousystem.User
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		
		//2、提交流程
		try {
			tramsactionTemplate.execute(new TransactionCallback() {

				public Object doInTransaction(TransactionStatus status) {
					WorkflowStatement stmt = workflowSession
							.createWorkflowStatement();
					try {
						WorkItem wi = stmt.claimWorkItem(workItemId);
						return wi;
					} catch (InvalidOperationException e) {
						throw new RuntimeException(e);
					}
				}

			});
		} catch (Exception e) {
			forwardToErrorPage(req, resp, "提交流程失败，当前工作项是[workItemId=" + workItemId
					+ "]。", e);
			return;
		}
		
		//3、导航到指定页面，缺省为我的待办页面
		String forwardURL = req.getParameter(Constants.FORWARD_URL);
		if (forwardURL==null || forwardURL.trim().equals("")){
			forwardURL = "/servlet/WorkflowOperationServlet?"+Constants.ACTION_TYPE+"="+Constants.LIST_TODO_WORKITEMS;
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardURL);
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 查询抄送信息，
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _listReadOnlyWorkItems(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
			.createWorkflowSession(fireContext, u);
		
		WorkflowQuery<WorkItem> query = workflowSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, u.getId()))
			.add(Restrictions.eq(WorkItemProperty.STATE, WorkItemState.READONLY))
		.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		
		List<WorkItem> workItemList = query.list();
		req.setAttribute("workItemList", workItemList);
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_client/my_readonly_workitems.jsp");
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 查询当前用户的待办工作项
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _listMyTodoWorkItems(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
			.createWorkflowSession(fireContext, u);
		
		WorkflowQuery<WorkItem> query = workflowSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, u.getId()))
			.add(Restrictions.lt(WorkItemProperty.STATE, WorkItemState.DELIMITER))
		.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		
		List<WorkItem> workItemList = query.list();
		req.setAttribute("workItemList", workItemList);
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_client/my_todo_workitems.jsp");
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 查询当前用户的已办工作项，此处包含正常完成的，被取消的，委派给他人的等等。
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _listMyHaveDoneWorkItems(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
			.createWorkflowSession(fireContext, u);
		
		WorkflowQuery<WorkItem> query = workflowSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.OWNER_ID, u.getId()))
			.add(Restrictions.gt(WorkItemProperty.STATE, WorkItemState.DELIMITER))
			.add(Restrictions.ne(WorkItemProperty.STATE, WorkItemState.READONLY))
		.addOrder(Order.desc(WorkItemProperty.END_TIME));
		
		List<WorkItem> workItemList = query.list();
		req.setAttribute("workItemList", workItemList);
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_client/my_havedone_workitems.jsp");
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 打开当前用户发起的在办流程实例
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _listMyActiveProcessInstance(final HttpServletRequest req, final HttpServletResponse resp)
	throws ServletException, IOException {
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
			.createWorkflowSession(fireContext, u);
		
		WorkflowQuery<WorkItem> query = workflowSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.PROC_INST_CREATOR_ID, u.getId()))
			.add(Restrictions.lt(WorkItemProperty.STATE, WorkItemState.DELIMITER))
		.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		
		List<WorkItem> workItemList = query.list();
		req.setAttribute("workItemList", workItemList);
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_client/my_active_process_instance.jsp");
		dispatcher.forward(req, resp);
	}
	
	/**
	 * 查询同一流程实例的所有工作项
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void _listWorkItemsInProcessInstance(final HttpServletRequest req, final HttpServletResponse resp)
	throws ServletException, IOException {
		String processInstanceId = req.getParameter(Constants.PROCESS_INSTANCE_ID);
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		
		WorkflowQuery<WorkItem> query = workflowSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, processInstanceId))
			.addOrder(Order.desc(WorkItemProperty.STEP_NUMBER));
		
		List<WorkItem> workItemList = query.list();
		req.setAttribute("workItemList", workItemList);
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_client/work_history.jsp");
		dispatcher.forward(req, resp);
	}
	/**
	 * 结束工作项
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void _completeWorkItem(final HttpServletRequest req, final HttpServletResponse resp)
	throws ServletException, IOException {
		final String workItemId = req.getParameter(Constants.WORKITEM_ID);
		
		final String targetActivityId = req.getParameter(Constants.TARGET_ACTIVITY_ID);
		
		//获取下一步操作者列表
		final Map<String,AssignmentHandler> assignmentHandlers = createAssignmentStrategy(req);
	
		
		//审批结论、审批意见
		String approvalId = req.getParameter(WorkItemProperty.ATTACHMENT_ID.getPropertyName());
		if (approvalId==null || approvalId.trim().equals("")){
			approvalId = (String)req.getAttribute(WorkItemProperty.ATTACHMENT_ID.getPropertyName());
		}
		String approvalDetail = req.getParameter(WorkItemProperty.NOTE.getPropertyName());
		if (approvalDetail==null || approvalDetail.trim().equals("")){
			approvalDetail = (String)req.getAttribute(WorkItemProperty.NOTE.getPropertyName());
		}
		final String __approvalId = approvalId;
		final String __approvalDetail = approvalDetail;
		

		
		//1、获得当前系统登录用户并创建Session；系统登录用户必须实现org.fireflow.engine.modules.ousystem.User
		final User u = getCurrentUser(req);
		
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		
		//2、提交流程
		try {
			tramsactionTemplate.execute(new TransactionCallback() {

				public Object doInTransaction(TransactionStatus status) {
					WorkflowStatement stmt = workflowSession
							.createWorkflowStatement();
					try {
						if (targetActivityId == null
								|| targetActivityId.trim().equals("")) {
							stmt.completeWorkItem(workItemId,
									assignmentHandlers,__approvalId,null,__approvalDetail);
						} else {
							stmt.completeWorkItemAndJumpTo(workItemId,
									targetActivityId, assignmentHandlers,__approvalId,null,__approvalDetail);
						}
					} catch (InvalidOperationException e) {
						throw new RuntimeException(e);
					}

					return null;
				}

			});
		} catch (Exception e) {
			forwardToErrorPage(req, resp, "提交流程失败，当前工作项是[workItemId=" + workItemId
					+ "]。", e);
			return;
		}
		
		//3、导航到指定页面，缺省为我的待办页面
		String forwardURL = req.getParameter(Constants.FORWARD_URL);
		if (forwardURL==null || forwardURL.trim().equals("")){
			forwardURL = "/servlet/WorkflowOperationServlet?"+Constants.ACTION_TYPE+"="+Constants.LIST_TODO_WORKITEMS;
		}
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardURL);
		dispatcher.forward(req, resp);
	}
	
	private Map<String,AssignmentHandler> createReassignmentHandler(HttpServletRequest req){
		Map<String,AssignmentHandler> assignmentStrategy = new HashMap<String,AssignmentHandler>();
		Map<String,List<User>> allActors = new HashMap<String,List<User>>();
		
		Enumeration enumeration = req.getParameterNames();
		while(enumeration.hasMoreElements()){
			String name = (String)enumeration.nextElement();
			if (name!=null && name.startsWith(Constants.NEXT_ACTOR_INPUT_NAME_PREFIX)){
				String[] actorsInfo = req.getParameterValues(name);
				String activityId = name.substring(Constants.NEXT_ACTOR_INPUT_NAME_PREFIX.length());
				List<User> tmpUsers = new ArrayList<User>();
				if (actorsInfo!=null && actorsInfo.length>0){
					for (String actorInfo : actorsInfo){
						User u = parseUser(actorInfo);
						tmpUsers.add(u);
					}						
				}
				
				List<User> users = allActors.get(activityId);
				if (users==null){
					users = new ArrayList<User>();
					allActors.put(activityId, users);
				}
				users.addAll(tmpUsers);
			}
		}
		
		Iterator<String> keys = allActors.keySet().iterator();
		while (keys!=null && keys.hasNext()){
			String activityId = keys.next();
			List<User> users = allActors.get(activityId);
			ReassignmentHandler handler = new ReassignmentHandler();
			handler.setPotentialOwners(users);
			assignmentStrategy.put(activityId, handler);
			//TODO 分配方式待完善
		}
		
		return assignmentStrategy;
	}
	
	private Map<String,AssignmentHandler> createAssignmentStrategy(HttpServletRequest req){
//		String has_customized_actors = req.getParameter(Constants.HAS_CUSTOMIZED_ACTORS);
//		if (has_customized_actors!=null && has_customized_actors.trim().equalsIgnoreCase("true")){
			Map<String,AssignmentHandler> assignmentStrategy = new HashMap<String,AssignmentHandler>();
			Map<String,List<User>> allActors = new HashMap<String,List<User>>();
			
			Enumeration enumeration = req.getParameterNames();
			while(enumeration.hasMoreElements()){
				String name = (String)enumeration.nextElement();
				if (name!=null && name.startsWith(Constants.NEXT_ACTOR_INPUT_NAME_PREFIX)){
					String[] actorsInfo = req.getParameterValues(name);
					String activityId = name.substring(Constants.NEXT_ACTOR_INPUT_NAME_PREFIX.length());
					List<User> tmpUsers = new ArrayList<User>();
					if (actorsInfo!=null && actorsInfo.length>0){
						for (String actorInfo : actorsInfo){
							User u = parseUser(actorInfo);
							tmpUsers.add(u);
						}						
					}
					
					List<User> users = allActors.get(activityId);
					if (users==null){
						users = new ArrayList<User>();
						allActors.put(activityId, users);
					}
					users.addAll(tmpUsers);
				}
			}
			
			Iterator<String> keys = allActors.keySet().iterator();
			while (keys!=null && keys.hasNext()){
				String activityId = keys.next();
				List<User> users = allActors.get(activityId);
				DynamicAssignmentHandler handler = new DynamicAssignmentHandler();
				handler.setPotentialOwners(users);
				assignmentStrategy.put(activityId, handler);
				//TODO 分配方式待完善
			}
			
			return assignmentStrategy;
//		}
//		return null;
	}
	
	private User parseUser(String actorInfo){
		if (actorInfo==null || actorInfo.trim().length()==0){
			return null;
		}
		UserImpl u = new UserImpl();
		StringTokenizer stoken = new StringTokenizer(actorInfo,"~");
		if (stoken.hasMoreTokens()){
			u.setId(stoken.nextToken());
		}
		if (stoken.hasMoreTokens()){
			u.setName(stoken.nextToken());
		}
		if (stoken.hasMoreTokens()){
			u.setDeptId(stoken.nextToken());
		}
		if (stoken.hasMoreTokens()){
			u.setDeptName(stoken.nextToken());
		}
		return u;
	}
	
	/**
	 * 创建流程实例，但是并不启动该流程实例
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void _createProcessInstance(final HttpServletRequest req, final HttpServletResponse resp)
		throws ServletException, IOException {

		final String processId = req.getParameter(Constants.PROCESS_ID);
		
		//1、获得当前系统登录用户并创建Session；系统登录用户必须实现org.fireflow.engine.modules.ousystem.User
		final User u = getCurrentUser(req);
		final WorkflowSession workflowSession = WorkflowSessionFactory.createWorkflowSession(fireContext, u);

		//2、获得流程定义
		ProcessLoadStrategy processLoadStrategy = fireContext.getEngineModule(ProcessLoadStrategy.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ProcessRepository tempRepository = null;
		try {
			User wfUser = workflowSession.getCurrentUser();
			tempRepository = processLoadStrategy.findTheProcessForRunning( processId, FpdlConstants.PROCESS_TYPE_FPDL20,wfUser,workflowSession);
		} catch (InvalidModelException e1) {
			forwardToErrorPage(req,resp,"读取流程定义发生错误，流程id="+processId,e1);
			return;
		}
		final ProcessRepository processRepository = tempRepository;
		WorkflowProcess workflowProcess = processRepository==null?null:(WorkflowProcess)processRepository.getProcessObject();
		if (workflowProcess==null){
			forwardToErrorPage(req,resp,"没有找到相关的流程，流程id="+processId,null);
			return;
		}
		
		
		//3、获得起始节点的表单信息
		String firstActivityId = req.getParameter(Constants.FIRST_ACTIVITY_ID);
		if (firstActivityId==null || firstActivityId.trim().equals("")){
			forwardToErrorPage(req,resp,"request参数中没有FIRST_ACTIVITY_ID或者传入的FIRST_ACTIVITY_ID值为空",null);
			return ;
		}
		final Activity activity = workflowProcess.getMainSubProcess().getActivity(firstActivityId);
		if (activity==null){
			forwardToErrorPage(req,resp,"流程[processId="+processId+"]中没有id为"+firstActivityId+"的Activity。",null);
			return ;
		}
		if (activity.getServiceBinding()==null || activity.getServiceBinding().getServiceId()==null){
			forwardToErrorPage(req,resp,"活动[activityId="+activity.getId()+",displayName="+activity.getDisplayName()+"]中没有邦定服务。",null);
			return ;
		}
		final ServiceDef serviceDef = workflowProcess.getService(activity.getServiceBinding().getServiceId());
		if (serviceDef==null || !(serviceDef instanceof HumanService)){
			forwardToErrorPage(req,resp,"流程[processId="+processId+"]中没有找到id="+activity.getServiceBinding().getServiceId()+"的服务；或者该服务不是人工任务。",null);
			return ;
		}
		HumanService humanService = (HumanService)serviceDef;
		String formUrl = humanService.getFormUrl();
		
		//4、创建流程实例（注意，仅是创建，但不启动）		
		ProcessInstance processInstance = null;
		final Map<String,Object> theInputValues = new HashMap<String,Object>();
		try {
			processInstance = (ProcessInstance) tramsactionTemplate
					.execute(new TransactionCallback() {

						public Object doInTransaction(TransactionStatus status) {

							WorkflowStatement workflowStatement = workflowSession
									.createWorkflowStatement();
							try {
								ProcessInstance processInstance = workflowStatement
										.createProcessInstance(
												processRepository
														.getProcessId(),
												processRepository.getVersion());
								
								ServiceBinding serviceBinding = activity.getServiceBinding();
								Map<String,Object> inputsValues = AbsServiceInvoker.resolveInputAssignments(fireContext,workflowSession,processInstance,null,serviceBinding,serviceDef);
								if (inputsValues!=null){
									theInputValues.putAll(inputsValues);
								}
								return processInstance;
							} catch (InvalidModelException e) {
								throw new RuntimeException(e);
							} catch (WorkflowProcessNotFoundException e) {
								throw new RuntimeException(e);
							} catch(ScriptException e){
								throw new RuntimeException(e);
							}
						}

					});
		} catch (Exception e) {
			forwardToErrorPage(req, resp, "创建流程实例[processId=" + processId
					+ "]失败。", e);
			return;
		}
		
		//5、设置request attribute
		if (theInputValues.size()>0){
			Iterator<Entry<String,Object>> iterator = theInputValues.entrySet().iterator();
			while (iterator.hasNext()){
				Entry<String,Object> entry = iterator.next();
				req.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		
		
		//6、跳转到表单页面，让用户补充业务信息
		req.setAttribute(Constants.PROCESS_INSTANCE_ID, processInstance.getId());
		RequestDispatcher dispatcher = req.getRequestDispatcher(formUrl);
		dispatcher.forward(req, resp);
	}
	
	private void forwardToErrorPage(HttpServletRequest req, HttpServletResponse resp,String message,Throwable exception)
		throws ServletException, IOException {
		req.setAttribute(Constants.ERROR_MESSAGE, message);
		req.setAttribute(Constants.ERROR_STACK, Utils.exceptionStackToString(exception));
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/common/error_message.jsp");
		dispatcher.forward(req, resp);
	}
	
	private User getCurrentUser(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		return WorkflowUtil.getCurrentWorkflowUser(session);
	}
}
