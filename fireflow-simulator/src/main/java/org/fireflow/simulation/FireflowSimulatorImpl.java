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
package org.fireflow.simulation;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowQueryImpl;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.engine.modules.formsystem.Form;
import org.fireflow.engine.modules.formsystem.FormCategory;
import org.fireflow.engine.modules.formsystem.FormSystemConnector;
import org.fireflow.engine.modules.formsystem.impl.FormCategoryImpl;
import org.fireflow.engine.modules.formsystem.impl.FormImpl;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.DepartmentImpl;
import org.fireflow.engine.modules.ousystem.impl.GroupImpl;
import org.fireflow.engine.modules.ousystem.impl.RoleImpl;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.server.WorkflowEngineServiceInternalImpl;
import org.fireflow.server.support.MapConvertor;
import org.fireflow.server.support.ObjectWrapper;
import org.fireflow.server.support.PropertiesConvertor;
import org.fireflow.server.support.ScopeBean;
import org.fireflow.simulation.client.SimulatorSessionFactory;
import org.fireflow.simulation.support.BreakPoint;
import org.fireflow.simulation.support.BreakPointContainer;
import org.firesoa.common.util.Utils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@WebService(name=WorkflowEngineService.PORT_TYPE,serviceName=WorkflowEngineService.SERVICE_LOCAL_NAME,
		portName=WorkflowEngineService.PORT_NAME,
		targetNamespace=WorkflowEngineService.TARGET_NAMESPACE,
		endpointInterface="org.fireflow.simulation.FireflowSimulator")
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
public class FireflowSimulatorImpl extends WorkflowEngineServiceInternalImpl 
implements FireflowSimulator{
	public static final String ARG_PREFIX_PROCESS_FILE = "-processfile=";
	public static final String ARG_PREFIX_SUBPROCESS_ID = "-subprocessid=";
	public static final String ARG_PREFIX_SPRING_CONTEXT_FILES = "-springcontextfiles=";
	
	protected boolean outputOperationTip = true;
	
	protected BreakPointContainer breakPointContainer = null;
	
	protected SimulatorInitializer simulatorInitializer = null;
	
	
	public SimulatorInitializer getSimulatorInitializer() {
		return simulatorInitializer;
	}

	public void setSimulatorInitializer(SimulatorInitializer simulatorInitializer) {
		this.simulatorInitializer = simulatorInitializer;
	}

	public BreakPointContainer getBreakPointContainer() {
		return breakPointContainer;
	}

	public void setBreakPointContainer(BreakPointContainer breakPointContainer) {
		this.breakPointContainer = breakPointContainer;
	}

	public boolean isOutputOperationTip() {
		return outputOperationTip;
	}

	public void setOutputOperationTip(boolean outputOperationTip) {
		this.outputOperationTip = outputOperationTip;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#addBreakPoint(org.fireflow.simulation.support.BreakPoint)
	 */
	public void addBreakPoint(String sessionId,BreakPoint breakPoint) {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		breakPointContainer.addBreakPoint(breakPoint);
		return ;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#clearAllBreakPoint()
	 */
	public void clearAllBreakPoint(String sessionId) {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		breakPointContainer.clearAllBreakPoints();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#clearBreakPoint(org.fireflow.simulation.support.BreakPoint)
	 */
	public void clearBreakPoint(String sessionId,BreakPoint breakPoint) {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		breakPointContainer.clearBreakPoint(breakPoint);
		return ;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#getAllBreakPoint()
	 */
	public List<BreakPoint> getAllBreakPoint(String sessionId) {
		return breakPointContainer.getAllBreakPoints();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#forwardBreakPoint(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean forwardBreakPoint(String sessionId,BreakPoint breakpoint) throws EngineException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("断点继续执行");
		}
		if (breakpoint==null || breakpoint.getProcessId()==null || breakpoint.getElementId()==null){
			return false;
		}
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);
		
		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		WorkflowQuery<Token> query = sessionLocalImpl.createWorkflowQuery(Token.class);
		query.add(Restrictions.eq(TokenProperty.PROCESS_ID, breakpoint.getProcessId()))
				.add(Restrictions.eq(TokenProperty.ELEMENT_ID, breakpoint.getElementId()))
				.add(Restrictions.lt(TokenProperty.STATE, TokenState.DELIMITER));
		
		final Token token = query.unique();
		if (token==null){
			return false;//不用抛出异常，直接返回即可
		}
		
		return this.forwardToken(sessionLocalImpl, token);
	}
	
	public boolean forwardToken(String sessionId,String tokenId)throws EngineException{
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("断点继续执行");
		}
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);
		
		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		WorkflowQuery<Token> query = sessionLocalImpl.createWorkflowQuery(Token.class);
		
		final Token token = query.get(tokenId);

		if (token==null || token.getState()==null || 
				token.getState().getValue()>TokenState.DELIMITER.getValue()){
			return false;//不用抛出异常，直接返回即可
		}
		return this.forwardToken(sessionLocalImpl, token);
	}
	
	@SuppressWarnings("unchecked")
	protected boolean forwardToken(final WorkflowSessionLocalImpl sessionLocalImpl,final Token token){
		WorkflowQuery<ProcessInstance> q4ProcInst = sessionLocalImpl.createWorkflowQuery(ProcessInstance.class);
		ProcessInstance procInst = q4ProcInst.get(token.getProcessInstanceId());
		sessionLocalImpl.setCurrentProcessInstance(procInst);
		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		this.getTransactionTemplate().execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status) {
				KernelManager kernelManager = runtimeContext.getDefaultEngineModule(KernelManager.class);

				BookMark bookMark = new BookMark();
				bookMark.setToken(token);
				bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
				
				kernelManager.execute(sessionLocalImpl);
				
				return null;
			}
			
		});
		
		return true;
	}
	
	public void addBreakPointList(String sessionId,
			List<BreakPoint> breakPointList	){
		breakPointContainer.addAllBreanPoints(breakPointList);
		return ;
	}


	@Override
	public ProcessDescriptorImpl uploadProcessXml(String sessionId,
			String processXml,int version) throws EngineException {
		String charset  = Utils.findXmlCharset(processXml);
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("上传流程定义Xml,定义文件的字符集是"+charset);
		}
//		System.out.println("========java虚拟机的编码是");
//		System.out.println(System.getProperty("file.encoding"));
//		System.out.println("=====待上传的流程定义是:");
//		System.out.println(processXml);
//		System.out.println("==========================");
		return super.uploadProcessXml(sessionId, processXml, version);
	}

	@Override
	public AbsWorkflowEntity getEntity(String sessionId, String entityId,
			String entityClassName) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("返回流程实体[entityId="+entityId+",entityClass="+entityClassName+"]");
		}
		return super.getEntity(sessionId, entityId, entityClassName);
	}

	@Override
	public List<AbsWorkflowEntity> executeQueryList(String sessionId,
			WorkflowQueryImpl q) {
//		if (this.outputOperationTip){
//			SimulatorRunner.printOperationTip("查询符合条件的流程实体");
//		}
		return super.executeQueryList(sessionId, q);
	}

	@Override
	public int executeQueryCount(String sessionId, WorkflowQueryImpl q) {
//		if (this.outputOperationTip){
//			SimulatorRunner.printOperationTip("统计符合条件的流程实体数量");
//		}
		return super.executeQueryCount(sessionId, q);
	}

	@Override
	public ProcessInstanceImpl createProcessInstance1(String sessionId,
			String workflowProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建流程实例[workflowProcessId="+workflowProcessId+"]");
		}
		return super.createProcessInstance1(sessionId, workflowProcessId);
	}

	@Override
	public ProcessInstanceImpl createProcessInstance2(String sessionId,
			String workflowProcessId, int version)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建流程实例[workflowProcessId="+workflowProcessId+",version="+version+"]");
		}
		return super.createProcessInstance2(sessionId, workflowProcessId, version);
	}

	@Override
	public ProcessInstanceImpl createProcessInstance4(String sessionId,
			String workflowProcessId, int version, String subProcessId)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建流程实例[workflowProcessId="+workflowProcessId+",version="+version+"，subProcessId="+subProcessId+"]");
		}
		return super.createProcessInstance4(sessionId, workflowProcessId, version,
				subProcessId);
	}

	@Override
	public ProcessInstanceImpl createProcessInstance3(String sessionId,
			String workflowProcessId, String subProcessId)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建流程实例[workflowProcessId="+workflowProcessId+"，subProcessId="+subProcessId+"]");
		}
		return super.createProcessInstance3(sessionId, workflowProcessId, subProcessId);
	}

	@Override
	public ProcessInstanceImpl runProcessInstance(String sessionId,
			String processInstanceId, String bizId, MapConvertor mapConvertor) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("运行流程实例[processInstanceId="+processInstanceId+"]");
		}
		return super.runProcessInstance(sessionId, processInstanceId, bizId,
				mapConvertor);
	}

	@Override
	public ProcessInstanceImpl startProcess2(String sessionId,
			String workflowProcessId, int version, String bizId,
			MapConvertor mapConvertor) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建并运行流程实例[workflowProcessId="+workflowProcessId+"，version="+version+"]");
		}
		return super.startProcess2(sessionId, workflowProcessId, version, bizId,
				mapConvertor);
	}

	@Override
	public ProcessInstanceImpl startProcess4(String sessionId,
			String workflowProcessId, int version, String subProcessId,
			String bizId, MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建并运行流程实例[workflowProcessId="+workflowProcessId+"，version="+version+",subProcessId="+subProcessId+"]");
		}
		return super.startProcess4(sessionId, workflowProcessId, version, subProcessId,
				bizId, mapConvertor);
	}

	@Override
	public ProcessInstanceImpl startProcess1(String sessionId,
			String workflowProcessId, String bizId, MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建并运行流程实例[workflowProcessId="+workflowProcessId+"]");
		}
		return super.startProcess1(sessionId, workflowProcessId, bizId, mapConvertor);
	}

	@Override
	public ProcessInstanceImpl startProcess3(String sessionId,
			String workflowProcessId, String subProcessId, String bizId,
			MapConvertor mapConvertor) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("创建并运行流程实例[workflowProcessId="+workflowProcessId+"，subProcessId="+subProcessId+"]");
		}
		return super.startProcess3(sessionId, workflowProcessId, subProcessId, bizId,
				mapConvertor);
	}

	@Override
	public ActivityInstanceImpl abortActivityInstance(String sessionId,
			String activityInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("撤销活动实例[activityInstanceId="+activityInstanceId+"]");
		}
		return super.abortActivityInstance(sessionId, activityInstanceId, note);
	}

	@Override
	public ActivityInstanceImpl suspendActivityInstance(String sessionId,
			String activityInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("挂起活动实例[activityInstanceId="+activityInstanceId+"]");
		}
		return super.suspendActivityInstance(sessionId, activityInstanceId, note);
	}

	@Override
	public ActivityInstanceImpl restoreActivityInstance(String sessionId,
			String activityInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("恢复活动实例[activityInstanceId="+activityInstanceId+"]");
		}
		return super.restoreActivityInstance(sessionId, activityInstanceId, note);
	}

	@Override
	public ProcessInstanceImpl abortProcessInstance(String sessionId,
			String processInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("撤销流程实例[processInstanceId="+processInstanceId+"]");
		}
		return super.abortProcessInstance(sessionId, processInstanceId, note);
	}

	@Override
	public ProcessInstanceImpl suspendProcessInstance(String sessionId,
			String processInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("挂起流程实例[processInstanceId="+processInstanceId+"]");
		}
		return super.suspendProcessInstance(sessionId, processInstanceId, note);
	}

	@Override
	public ProcessInstanceImpl restoreProcessInstance(String sessionId,
			String processInstanceId, String note)
			throws InvalidOperationException {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("恢复流程实例[processInstanceId="+processInstanceId+"]");
		}
		return super.restoreProcessInstance(sessionId, processInstanceId, note);
	}

	@Override
	public LocalWorkItemImpl claimWorkItem(String sessionId, String workItemId) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("签收工作项[workItemId="+workItemId+"]");
		}
		return super.claimWorkItem(sessionId, workItemId);
	}

	@Override
	public LocalWorkItemImpl withdrawWorkItem(String sessionId, String workItemId) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("取回工作项[workItemId="+workItemId+"]");
		}
		return super.withdrawWorkItem(sessionId, workItemId);
	}

	@Override
	public LocalWorkItemImpl disclaimWorkItem(String sessionId, String workItemId,
			String attachmentId, String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("退签收工作项[workItemId="+workItemId+"]");
		}
		return super.disclaimWorkItem(sessionId, workItemId, attachmentId,
				attachmentType, note);
	}

	@Override
	public LocalWorkItemImpl completeWorkItem1(String sessionId, String workItemId,
			String attachmentId, String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("结束工作项[workItemId="+workItemId+"]");
		}
		return super.completeWorkItem1(sessionId, workItemId, attachmentId,
				attachmentType, note);
	}

	@Override
	public LocalWorkItemImpl completeWorkItem2(String sessionId, String workItemId,
			MapConvertor mapConvertor, String attachmentId,
			String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("结束工作项[workItemId="+workItemId+"]");
		}
		return super.completeWorkItem2(sessionId, workItemId, mapConvertor,
				attachmentId, attachmentType, note);
	}

	@Override
	public LocalWorkItemImpl completeWorkItemAndJumpTo1(String sessionId,
			String workItemId, String targetActivityId, String attachmentId,
			String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("结束工作项并条跳转[workItemId="+workItemId+",targetActivityId="+targetActivityId+"]");
		}
		return super.completeWorkItemAndJumpTo1(sessionId, workItemId,
				targetActivityId, attachmentId, attachmentType, note);
	}

	@Override
	public LocalWorkItemImpl completeWorkItemAndJumpTo2(String sessionId,
			String workItemId, String targetActivityId,
			MapConvertor assignmentStrategy, String attachmentId,
			String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("结束工作项并条跳转[workItemId="+workItemId+",targetActivityId="+targetActivityId+"]");
		}
		return super.completeWorkItemAndJumpTo2(sessionId, workItemId,
				targetActivityId, assignmentStrategy, attachmentId, attachmentType,
				note);
	}

	@Override
	public LocalWorkItemImpl reassignWorkItemTo(String sessionId, String workItemId,
			ReassignmentHandler handler, String attachmentId,
			String attachmentType, String note) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("委派工作项[workItemId="+workItemId+"]");
		}
		return super.reassignWorkItemTo(sessionId, workItemId, handler, attachmentId,
				attachmentType, note);
	}

	@Override
	public ObjectWrapper getVariableValue(String sessionId,
			ScopeBean scopeBean, String varName) {
//		if (this.outputOperationTip){
//			SimulatorRunner.printOperationTip("获取流程变量[varName="+varName+"]");
//		}
		return super.getVariableValue(sessionId, scopeBean, varName);
	}

	@Override
	public MapConvertor getVariableValues(String sessionId, ScopeBean scopeBean) {
//		if (this.outputOperationTip){
//			SimulatorRunner.printOperationTip("获取流程变量集合[processElementId="+scopeBean.getProcessElementId()+"]");
//		}
		return super.getVariableValues(sessionId, scopeBean);
	}

	@Override
	public void setVariableValue1(String sessionId, ScopeBean scopeBean,
			String name, ObjectWrapper obj) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("设置流程变量["+name+"="+obj==null?null:obj.getValue()+"]");
		}
		super.setVariableValue1(sessionId, scopeBean, name, obj);
	}

	@Override
	public void setVariableValue2(String sessionId, ScopeBean scopeBean,
			String name, ObjectWrapper obj, PropertiesConvertor convertor) {
		if (this.outputOperationTip){
			SimulatorRunner.printOperationTip("设置流程变量["+name+"="+obj==null?null:obj.getValue()+"]");
		}
		super.setVariableValue2(sessionId, scopeBean, name, obj, convertor);
	}


	@SuppressWarnings("unchecked")
	public void initSimulator(@WebParam(name="sessionId")String sessionId){
//		if (this.outputOperationTip){
//			SimulatorRunner.printOperationTip("模拟器初始化");
//		}

		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		// 清除现有的数据
		springTransactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {

				simulatorInitializer.initSimulator();
				return null;
			}

		});
		
		//将现有的pvm中的PObject对象清除
		RuntimeContext rtCtx = sessionLocalImpl.getRuntimeContext();
		KernelManager kernelManager = rtCtx.getDefaultEngineModule(KernelManager.class);
		kernelManager.clearCachedPObject();
	}
	

	public List<FormCategoryImpl> findAllTopCategories(String sessionId){

		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		FormSystemConnector formSysConnector = runtimeContext.getDefaultEngineModule(FormSystemConnector.class);
		List<FormCategory> formCategoryList = formSysConnector.findAllTopCategories();
		List<FormCategoryImpl> result = new ArrayList<FormCategoryImpl>();
		if (formCategoryList!=null){
			for (FormCategory category: formCategoryList){
				result.add((FormCategoryImpl)category);
			}
		}

		
		return result;
	}
	
	
	public List<FormCategoryImpl> findChildCategories(String sessionId,
			String parentCategoryId){
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		FormSystemConnector formSysConnector = runtimeContext.getDefaultEngineModule(FormSystemConnector.class);
		List<FormCategory> formCategoryList = formSysConnector.findChildCategories(parentCategoryId);
		List<FormCategoryImpl> result = new ArrayList<FormCategoryImpl>();
		if (formCategoryList!=null){
			for (FormCategory category: formCategoryList){
				result.add((FormCategoryImpl)category);
			}
		}

		
		return result;
	}
	
	
	public List<FormImpl> findForms(String sessionId,
			String categoryId){
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		FormSystemConnector formSysConnector = runtimeContext.getDefaultEngineModule(FormSystemConnector.class);
		List<Form> formList = formSysConnector.findForms(categoryId);
		List<FormImpl> result = new ArrayList<FormImpl>();
		if (formList!=null){
			for (Form form: formList){
				result.add((FormImpl)form);
			}
		}

		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#getFormById(java.lang.String, java.lang.String)
	 */
	public FormImpl getFormById(String sessionId, String formId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		FormSystemConnector formSysConnector = runtimeContext.getDefaultEngineModule(FormSystemConnector.class);

		Form form = formSysConnector.getFormById(formId);
		return (FormImpl)form;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#getCategoryById(java.lang.String, java.lang.String)
	 */
	public FormCategoryImpl getCategoryById(String sessionId, String categoryId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		FormSystemConnector formSysConnector = runtimeContext.getDefaultEngineModule(FormSystemConnector.class);

		FormCategory category = formSysConnector.getCategoryById(categoryId);
		
		return (FormCategoryImpl)category;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findUserById(java.lang.String, java.lang.String)
	 */
	public UserImpl findUserById(String sessionId, String userId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);

		User user = ouSysConnector.findUserById(userId);
		
		if (user instanceof UserImpl){
			return (UserImpl)user;
		}else{
			UserImpl u = new UserImpl();
			u.copy(user);
			return u;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findUsersInDepartment(java.lang.String, java.lang.String)
	 */
	public List<UserImpl> findUsersInDepartment(String sessionId, String deptId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<User> userList = ouSysConnector.findUsersInDepartment(deptId);
		
		List<UserImpl> result = new ArrayList<UserImpl>();
		if (userList!=null){
			for (User user : userList){
				if (user instanceof UserImpl){
					result.add((UserImpl)user);
				}else{
					UserImpl u = new UserImpl();
					u.copy(user);
					result.add(u);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findUsersInRole(java.lang.String, java.lang.String)
	 */
	public List<UserImpl> findUsersInRole(String sessionId, String roleId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<User> userList = ouSysConnector.findUsersInRole(roleId);
		
		List<UserImpl> result = new ArrayList<UserImpl>();
		if (userList!=null){
			for (User user : userList){
				if (user instanceof UserImpl){
					result.add((UserImpl)user);
				}else{
					UserImpl u = new UserImpl();
					u.copy(user);
					result.add(u);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findUsersInGroup(java.lang.String, java.lang.String)
	 */
	public List<UserImpl> findUsersInGroup(String sessionId, String groupId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<User> userList = ouSysConnector.findUsersInGroup(groupId);
		
		List<UserImpl> result = new ArrayList<UserImpl>();
		if (userList!=null){
			for (User user : userList){
				if (user instanceof UserImpl){
					result.add((UserImpl)user);
				}else{
					UserImpl u = new UserImpl();
					u.copy(user);
					result.add(u);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findDepartmentById(java.lang.String, java.lang.String)
	 */
	public DepartmentImpl findDepartmentById(String sessionId, String id) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		Department department = ouSysConnector.findDepartmentById(id);
		if (department instanceof DepartmentImpl){
			return (DepartmentImpl)department;
		}else{
			DepartmentImpl dept = new DepartmentImpl();
			dept.copy(department);
			return dept;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findAllTopDepartments(java.lang.String)
	 */
	public List<DepartmentImpl> findAllTopDepartments(String sessionId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Department> departmentList = ouSysConnector.findAllTopDepartments();
		List<DepartmentImpl> result = new ArrayList<DepartmentImpl>();
		if (departmentList!=null){
			for (Department department : departmentList){
				if (department instanceof DepartmentImpl){
					result.add((DepartmentImpl)department);
				}else{
					DepartmentImpl dept = new DepartmentImpl();
					dept.copy(department);
					result.add( dept);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findChildDepartments(java.lang.String, java.lang.String)
	 */
	public List<DepartmentImpl> findChildDepartments(String sessionId,
			String parentId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Department> departmentList = ouSysConnector.findChildDepartments(parentId);
		List<DepartmentImpl> result = new ArrayList<DepartmentImpl>();
		if (departmentList!=null){
			for (Department department : departmentList){
				if (department instanceof DepartmentImpl){
					result.add((DepartmentImpl)department);
				}else{
					DepartmentImpl dept = new DepartmentImpl();
					dept.copy(department);
					result.add( dept);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findRoleById(java.lang.String, java.lang.String)
	 */
	public RoleImpl findRoleById(String sessionId, String id) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		Role role = ouSysConnector.findRoleById(id);
		if (role instanceof RoleImpl){
			return (RoleImpl)role;
		}else{
			RoleImpl r = new RoleImpl();
			r.copy(role);
			return r;
			
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findAllTopRoles(java.lang.String)
	 */
	public List<RoleImpl> findAllTopRoles(String sessionId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Role> roleList = ouSysConnector.findAllTopRoles();
		List<RoleImpl> result = new ArrayList<RoleImpl>();
		if (roleList!=null){
			for (Role role : roleList){
				if (role instanceof RoleImpl){
					result.add((RoleImpl)role);
				}else{
					RoleImpl r = new RoleImpl();
					r.copy(role);
					result.add(r);
					
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findChildRoles(java.lang.String, java.lang.String)
	 */
	public List<RoleImpl> findChildRoles(String sessionId, String parentRoleId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Role> roleList = ouSysConnector.findChildRoles(parentRoleId);
		List<RoleImpl> result = new ArrayList<RoleImpl>();
		if (roleList!=null){
			for (Role role : roleList){
				if (role instanceof RoleImpl){
					result.add((RoleImpl)role);
				}else{
					RoleImpl r = new RoleImpl();
					r.copy(role);
					result.add(r);
					
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findRolesInDepartment(java.lang.String, java.lang.String)
	 */
	public List<RoleImpl> findRolesInDepartment(String sessionId, String deptId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Role> roleList = ouSysConnector.findRolesInDepartment(deptId);
		List<RoleImpl> result = new ArrayList<RoleImpl>();
		if (roleList!=null){
			for (Role role : roleList){
				if (role instanceof RoleImpl){
					result.add((RoleImpl)role);
				}else{
					RoleImpl r = new RoleImpl();
					r.copy(role);
					result.add(r);
					
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findGroupById(java.lang.String, java.lang.String)
	 */
	public GroupImpl findGroupById(String sessionId, String id) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		Group group = ouSysConnector.findGroupById(id);
		if (group instanceof GroupImpl){
			return (GroupImpl)group;
		}else{
			GroupImpl g = new GroupImpl();
			g.copy(group);
			return g;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findAllGroups(java.lang.String)
	 */
	public List<GroupImpl> findAllGroups(String sessionId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Group> groupList = ouSysConnector.findAllGroups();
		List<GroupImpl> result = new ArrayList<GroupImpl>();
		
		if (groupList!=null){
			for (Group group : groupList){
				if (group instanceof GroupImpl){
					result.add((GroupImpl)group);
				}else{
					GroupImpl g = new GroupImpl();
					g.copy(group);
					result.add(g);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.FireflowSimulator#findGroupsInDepartment(java.lang.String, java.lang.String)
	 */
	public List<GroupImpl> findGroupsInDepartment(String sessionId,
			String deptId) {
		final WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);

		final RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();
		
		OUSystemConnector ouSysConnector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		List<Group> groupList = ouSysConnector.findGroupsInDepartment(deptId);
		List<GroupImpl> result = new ArrayList<GroupImpl>();
		
		if (groupList!=null){
			for (Group group : groupList){
				if (group instanceof GroupImpl){
					result.add((GroupImpl)group);
				}else{
					GroupImpl g = new GroupImpl();
					g.copy(group);
					result.add(g);
				}
			}
		}
		return result;
	}
	
	public WorkflowSessionLocalImpl login(String userName, String password) throws EngineException{
		if (SimulatorSessionFactory.SIMULATOR_DEFAULT_USER_NAME.equals(userName)){
			UserImpl u = new UserImpl();
			u.setDeptId("FireflowSimulationDept");
			u.setDeptName("FireflowSimulationDept");
			u.setId(SimulatorSessionFactory.SIMULATOR_DEFAULT_USER_NAME);
			u.setName("FireWorkflow模拟器");
			
			WorkflowSessionLocalImpl session = (WorkflowSessionLocalImpl)WorkflowSessionFactory.createWorkflowSession(runtimeContext, u);

			if (cacheManager!=null){
				Cache cache = cacheManager.getCache(SESSION_CACHE);
				if (cache!=null){
					cache.put(new Element(session.getSessionId(),session));
				}
				
			}
			return session;
		}else{
			return super.login(userName, password);
		}
	}
	
}
