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
package org.fireflow.client;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.RepositoryDescriptor;
//import org.fireflow.engine.entity.repository.ResourceDescriptor;
//import org.fireflow.engine.entity.repository.ResourceDescriptorProperty;
//import org.fireflow.engine.entity.repository.ServiceDescriptor;
//import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.KernelException;

/**
 * 使用模式：
 * WorkflowStatement queryDelegate = workflowSession.createWorkflowStatement();
 * queryDelegate.setAttribute("a","abc")
 *          .setDynamicAssignmentHandler("activity2",theAssigmentHandler)
 *          .startProcess("process1");
 * ActivityInstance activityInstance = queryDelegate.getCurrentActivityInstance();
 * List<WorkItem> workitemList = queryDelegate.getLatestCreatedWorkItems();
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface WorkflowStatement {
	/**
	 * 获得当前操作的流程类型
	 * @return
	 */
	public String getProcessType();
	
	//获得当前的流程实例
	//该方法在远程接口中不合理，因此在接口中注销
	//public ProcessInstance getCurrentProcessInstance();
	
	//获得当前的活动实例。
	//该方法在远程接口中不合理，因此在接口中注销
//	public ActivityInstance getCurrentActivityInstance();
	
	//获得最近一次流程操作所创建的所有的工作项。
	//该方法用于远程接口不合理，因为新产生的工作项可能非常多，通过该方法返回会导致
	//网络通信障碍，因此在接口中注销。
	//public List<WorkItem> getLatestCreatedWorkItems();
	
	

//	/**
//	 * 设置一个动态任务分配处理句柄。该方法实质是调用WorkflowSession.setDynamicAssignmentHandler(String activityId,AssignmentHandler assignmentHandler);
//	 * @param dynamicAssignmentHandler
//	 */
//   通过调用completeWorkItem(...)或者completeWorkItemAndJumpTo(...)设置该参数，
//   不必单独暴露该方法
//	public WorkflowStatement setDynamicAssignmentHandler(String activityId,
//			AssignmentHandler dynamicAssignmentHandler);
	
	
	//
	// 类似HttpServletRequest的setAttribute,用于流程操作中传递参数。
	// 该方法实质是调用，WorkflowSession.setAttribute(String name ,Object attr);
	// @param name
	// @param attr
	// 在远程接口中，该方法不合理，因此从该接口注销。
	//	public WorkflowStatement setAttribute(String name ,Object attr);
		
	
    /******************************************************************************/
    /************                                                        **********/
    /************            与process instance 相关的API                **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/		
	
	/**
	 * 创建并启动流程。<br/>
	 * 该方法执行完毕后，可以通过WorkflowStatement.getCurrentProcessInstance(),
	 * WorkflowStatement.getCurrentActivityInstance(),WorkflowStatement.getLatestCreatedWorkItems()获得
	 * 当前的流程实例，活动实例和最新创建的工作项。注意，WorkflowStatement.getCurrentActivityInstance()返回的
	 * 活动实例不一定是流程第一个活动的实例；例如：如果流程第一个活动是一个java调用，而第二个活动是人工活动，
	 * 那么流程执行到人工活动才返回，所以WorkflowStatement.getCurrentActivityInstance()将返回人工活动的实例。
	 * 
	 * @param workflowProcessId  流程的Id。
	 * @param version 流程版本号
	 * @param bizId 业务实体主键。
	 * @param variables 流程变量
	 * @return 新创建的流程实例
	 * @throws InvalidModelException
	 */
	public ProcessInstance startProcess(String workflowProcessId,int version,String bizId,Map<String,Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException,InvalidOperationException;
	
	public ProcessInstance startProcess(String workflowProcessId,int version,String subProcessId,String bizId,Map<String,Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException,InvalidOperationException;
	
	public ProcessInstance startProcess(String workflowProcessId,String subProcessId,String bizId,Map<String,Object> variables) throws InvalidModelException,
	WorkflowProcessNotFoundException,InvalidOperationException;
	/**
	 * 创建并启动流程。具体启动哪个版本需要根据外部策略决定。<br/>
	 * 该方法执行完毕后，可以通过WorkflowStatement.getCurrentProcessInstance(),
	 * WorkflowStatement.getCurrentActivityInstance(),WorkflowStatement.getLatestCreatedWorkItems()获得
	 * 当前的流程实例，活动实例和最新创建的工作项。注意，WorkflowStatement.getCurrentActivityInstance()返回的
	 * 活动实例不一定是流程第一个活动的实例；例如：如果流程第一个活动是一个java调用，而第二个活动是人工活动，
	 * 那么流程执行到人工活动才返回，所以WorkflowStatement.getCurrentActivityInstance()将返回人工活动的实例。
	 * TODO 流程版本策略待斟酌。
	 * @param workflowProcessId 流程Id
	 * @param bizId 业务实体主键
	 * @param variables 流程变量
	 * @return 新创建的流程实例
	 * @throws InvalidModelException
	 * @throws WorkflowProcessNotFoundException
	 * @throws InvalidOperationException
	 */
	public ProcessInstance startProcess(String workflowProcessId,String bizId,Map<String,Object> variables) throws InvalidModelException,
	WorkflowProcessNotFoundException,InvalidOperationException;

	/**
	 * 创建并启动流程。该方法执行完毕后，可以通过WorkflowStatement.getCurrentProcessInstance(),
	 * WorkflowStatement.getCurrentActivityInstance(),WorkflowStatement.getLatestCreatedWorkItems()获得
	 * 当前的流程实例，活动实例和最新创建的工作项。注意，WorkflowStatement.getCurrentActivityInstance()返回的
	 * 活动实例不一定是流程第一个活动的实例；例如：如果流程第一个活动是一个java调用，而第二个活动是人工活动，
	 * 那么流程执行到人工活动才返回，所以WorkflowStatement.getCurrentActivityInstance()将返回人工活动的实例。
	 * @param process 流程定义对象
	 * @param processType 流程类型,名称为XPDL,FPDL,BPEL等
	 * @param bizId 业务实体主键
	 * @param variables 流程变量
	 * @return 新创建的流程实例
	 * @throws InvalidModelException
	 * @throws WorkflowProcessNotFoundException
	 * @throws InvalidOperationException
	 */
	public ProcessInstance startProcess(Object process,String bizId,Map<String,Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException,InvalidOperationException;
	
	public ProcessInstance createProcessInstance(String workflowProcessId) throws InvalidModelException ,WorkflowProcessNotFoundException;
	public ProcessInstance createProcessInstance(Object process)throws InvalidModelException ;
	public ProcessInstance createProcessInstance(String workflowProcessId,int version) throws InvalidModelException ,WorkflowProcessNotFoundException;
	public ProcessInstance createProcessInstance(String workflowProcessId,int version,String subProcessId) throws InvalidModelException ,WorkflowProcessNotFoundException;
	public ProcessInstance createProcessInstance(String workflowProcessId,String subProcessId)throws InvalidModelException ,WorkflowProcessNotFoundException;
	
	public ProcessInstance runProcessInstance(String processInstanceId,String bizId,Map<String,Object> variables) ;

	
	
    /**
     * 撤销流程实例。将流程实例、活动的TaskInstance、活动的WorkItem的状态设置为ABORTED。
     * 
     * @param processInstanceId 流程实例Id
     * @param note 备注信息。
     * @param 被撤销的流程实例
     */
    public ProcessInstance abortProcessInstance(String processInstanceId,String note)throws InvalidOperationException;

    /**
     * 挂起流程实例
     * @param processInstance
     */
    public ProcessInstance suspendProcessInstance(String processInstanceId,String note)throws InvalidOperationException;

    /**
     * 恢复被挂起的流程实例
     * @param processInstance
     */
    public ProcessInstance restoreProcessInstance(String processInstanceId,String note)throws InvalidOperationException;
//    
    /******************************************************************************/
    /************                                                        **********/
    /************            ActivityInstance相关的 API                      **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/		
	/**
	 * 将活动实例挂起
	 * @param activityInstanceId 活动实例Id
	 * @param 备注信息
	 * @return 被挂起的任务实例
	 */
    public ActivityInstance suspendActivityInstance(String activityInstanceId ,String note)throws InvalidOperationException;
    
    /**
     * 恢复被挂起的活动实例
	 * @param activityInstanceId 活动实例Id
	 * @param 备注信息
     * @return
     * @throws EngineException
     */
    public ActivityInstance restoreActivityInstance(String activityInstanceId,String note)throws InvalidOperationException;

    /**
     * 撤销活动实例
	 * @param activityInstanceId 活动实例Id
	 * @param 备注信息
     * @return
     * @throws InvalidOperationException
     */
    public ActivityInstance abortActivityInstance(String activityInstanceId,String note) throws InvalidOperationException;

	
    /******************************************************************************/
    /************                                                        **********/
    /************            workItem 相关的API                          **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/
	
    /**
     * 签收工作项。如果任务实例的分配模式是ANY，则同一个任务实例的其他工作项将被删除。
     * 如果任务是里的分配模式是ALL，则此操作不影响同一个任务实例的其他工作项的状态。<br/>
     * 如果签收成功，则返回一个新的WorkItem对象。<br/>
     * 如果签收失败，则返回null。<br/>
     * 例如：同一个TaskInstance被分配给Actor_1和Actor_2，且分配模式是ANY，即便Actor_1和Actor_2同时执行
     * 签收操作，也必然有一个人签收失败。系统对这种竞争性操作进行了同步。<br/>
     * 该方法和IWorkItem.claim()是等价的。
     * @param workItemId 被签收的工作项的Id
     * @return 如果签收成功，则返回被签收的IWorkItem对象；否则返回null
     * @throws org.fireflow.engine.exception.EngineException
     * @throws org.fireflow.kenel.KenelException
     * 
     */
	public WorkItem claimWorkItem(String workItemId) throws InvalidOperationException ;

	/**
	 * 
	 * @param workItemId
	 * @param attachmentId
	 * @param attachmentType
	 * @param note
	 * @return 如果退签收成功，则返回被退签的WorkItem，否则返回null
	 * @throws InvalidOperationException
	 */
	public WorkItem disclaimWorkItem(String workItemId,String attachmentId, String attachmentType, String note) throws InvalidOperationException ;
	/**
	 * 对已经结束的工作项执行取回操作。<br/>
	 * 只有满足如下约束才能正确执行取回操作：<br/>
	 * 1、下一个环节没有Tool类型或者Subflow类型的Task；<br/>
	 * 2、下一个环节Form类型的TaskInstance没有被签收。<br/>
	 * 如果在本WorkItem成功执行了jumpTo操作或者loopTo操作，只要满足上述条件，也可以
     * 成功执行withdraw。<br/>
	 * 该方法和IWorkItem.withdraw()等价
	 * @param workItemId 工作项Id
	 * @return 如果取回成功，则创建一个新的WorkItem 并返回该WorkItem
	 * @throws EngineException
	 * @throws KernelException
	 */
    public WorkItem withdrawWorkItem(String workItemId)throws InvalidOperationException;
//
//    /**
//     * 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
//     * 该操作必须满足如下条件：<br/>
//     * 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
//     * 2、没有合当前TaskInstance并行的其他TaskInstance；<br/>
//     * @param workItemId 工作项Id
//     * @throws EngineException
//     * @throws KernelException
//     */
//    public void rejectWorkItem(String workItemId)throws EngineException, KernelException;
//
//    /**
//     * 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
//     * 该操作必须满足如下条件：<br/>
//     * 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
//     * 2、没有合当前TaskInstance并行的其他TaskInstance；<br/>
//     * @param workItemId 工作项Id
//     * @param comments 备注信息
//     * @throws EngineException
//     * @throws KernelException
//     */    
//    public void rejectWorkItem(String workItemId,String comments)throws EngineException, KernelException;
//    
//    
//    
    /**
     * 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
     * 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
     * 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
     * 则结束当前TaskInstance<br/>
     * 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
     * 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
     * 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
     * @param workItemId 工作项Id
     * @param attachmentId TODO
     * @param attachmentType TODO
     * @param note TODO
     * @return TODO
     * @throws org.fireflow.engine.exception.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public WorkItem completeWorkItem(String workItemId,String attachmentId, String attachmentType, String note) throws InvalidOperationException;
    
    public WorkItem completeWorkItem(String workItemId,Map<String,AssignmentHandler> assignmentStrategy,String attachmentId, String attachmentType, String note)throws InvalidOperationException;

//    
    /**
     * 结束当前WorkItem，跳转到指定的Activity<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)当前Task的assignment为Task.ANY。或者当前Task的assignment为Task.ALL(汇签)，且本WorkItem结束后可以使得TaskInstance结束；与之相反的情况是，
     * 尚有其他参与汇签的操作者没有完成其工作项，这时engine拒绝跳转操作<br/>
     * 3)当前TaskInstance结束后,可以使得当前的ActivityInstance结束。与之相反的情况是，当前Activity包含了多个Task，且Activity的Complete Strategy是ALL，
     * 尚有其他的TaskInstance仍然处于活动状态，这种情况下执行jumpTo操作会被拒绝。
     * @param workItemId 工作项Id
     * @param targetActivityId 将要被启动的ActivityId
     * @param attachmentId TODO
     * @param attachmentType TODO
     * @param note TODO
     * @return TODO
     * @throws org.fireflow.engine.exception.EngineException 
     * @throws org.fireflow.kenel.KenelException
     */
    public WorkItem completeWorkItemAndJumpTo(String workItemId ,String targetActivityId,String attachmentId, String attachmentType, String note) throws InvalidOperationException;

    public WorkItem completeWorkItemAndJumpTo(String workItemId ,String targetActivityId,Map<String,AssignmentHandler> assignmentStrategy,String attachmentId, String attachmentType, String note) throws InvalidOperationException;
//
    /**
     * 将工作项委派给其他人，自己的工作项变成CANCELED状态
     * @param workItemId 工作项Id
     * @param reassignHandler ReassignmentHandler实例
     * @param attachmentId TODO
     * @param attachmentType TODO
     * @param note TODO
     * @return 新创建的工作项
     */    
    public WorkItem reassignWorkItemTo(String workItemId, ReassignmentHandler reassignHandler,String attachmentId, String attachmentType, String note) throws InvalidOperationException;
//    
//    /**
//     * 将工作项委派给其他人，自己的工作项变成CANCELED状态。返回新创建的工作项
//     * @param actorId 接受任务的操作员Id
//     * @param comments 相关的备注信息
//     * @return 新创建的工作项
//     */    
//    public WorkItem reassignWorkItemTo(String workItemId,String actorId,String comments) throws EngineException;
//	
//	
    /**
     * TODO 该方法需研究，斟酌。
     */
	//public void updateWorkItem(WorkItem workItem)throws InvalidOperationException;
    

	/*****************************************************************/
	/***************   流程定义相关的api ******************************/
	/*****************************************************************/
	//
	/**
	 * 发布流程定义或者覆盖已有的流程定义。<br/>
	 * 
	 * 当发布新的流程定义时，publishState默认取值为false，validDateFrom是当前时间，validDateTo是一个很大的日期数字2099-12-31，
	 * ownerId,ownerName,approver,approvedTime,updateLog均为空。<br/>
	 * 
	 * 当覆盖已有的流程定义时，上述字段的值均维持原值不变。<br/>
	 * 
	 * ProcessDescriptor相关字段含义如下：<br/>
	 * publishState:发布状态<br/>
	 * validDateFrom:流程有效期始<br/>
	 * validDateTo：流程有效期止<br/>
	 * ownerId:所有者id<br/>
	 * ownerName：所有者名称<br/>
	 * approver:审批人<br/>
	 * approvedTime:审批时间<br/>
	 * updateLog:发布日志<br/>
	 * 
	 * @param processObject WorkflowProcess对象。
	 * @param version 版本号。如果该参数值为0，则表示发布新的版本，系统自动计算一个新的版本号。
	 * 否则表示覆盖版本号为version的已存在的流程定义，或者插入一个版本号为version的流程定义。<br/>
	 * @return 流程定义描述符
	 * @throws InvalidModelException
	 */
	public ProcessDescriptor uploadProcessObject(Object processObject,int version) throws InvalidModelException;
	
	/**
	 * 发布流程定义或者覆盖已有的流程定义。<br/>
	 * 
	 * 当发布新的流程定义时，publishState默认取值为false，validDateFrom是当前时间，validDateTo是一个很大的日期数字2099-12-31，
	 * ownerId,ownerName,approver,approvedTime,updateLog均为空。<br/>
	 * 
	 * 当覆盖已有的流程定义时，上述字段的值均维持原值不变。<br/>
	 * 
	 * ProcessDescriptor相关字段含义如下：<br/>
	 * publishState:发布状态<br/>
	 * validDateFrom:流程有效期始<br/>
	 * validDateTo：流程有效期止<br/>
	 * ownerId:所有者id<br/>
	 * ownerName：所有者名称<br/>
	 * approver:审批人<br/>
	 * approvedTime:审批时间<br/>
	 * updateLog:发布日志<br/>
	 * 
	 * @param inStream input stream形态的流程定义文件。
	 * @param version 版本号。如果该参数值为0，则表示发布新的版本，系统自动计算一个新的版本号。
	 * 否则表示覆盖版本号为version的已存在的流程定义，或者插入一个版本号为version的流程定义。<br/>
	 * @return 流程定义描述符
	 * @throws InvalidModelException
	 */
	public ProcessDescriptor uploadProcessStream(InputStream inStream,int version) throws InvalidModelException;
	
	/**
	 * 发布流程定义或者覆盖已有的流程定义。<br/>
	 * 
	 * 当发布新的流程定义时，publishState默认取值为false，validDateFrom是当前时间，validDateTo是一个很大的日期数字2099-12-31，
	 * ownerId,ownerName,approver,approvedTime,updateLog均为空。<br/>
	 * 
	 * 当覆盖已有的流程定义时，上述字段的值均维持原值不变。<br/>
	 * 
	 * ProcessDescriptor相关字段含义如下：<br/>
	 * publishState:发布状态<br/>
	 * validDateFrom:流程有效期始<br/>
	 * validDateTo：流程有效期止<br/>
	 * ownerId:所有者id<br/>
	 * ownerName：所有者名称<br/>
	 * approver:审批人<br/>
	 * approvedTime:审批时间<br/>
	 * updateLog:发布日志<br/>
	 * 
	 * @param processXml xml格式的流程定义文件
	 * @param version 版本号。如果该参数值为0，则表示发布新的版本，系统自动计算一个新的版本号。
	 * 否则表示覆盖版本号为version的已存在的流程定义，或者插入一个版本号为version的流程定义。<br/>
	 * @return 流程定义描述符
	 * @throws InvalidModelException
	 */
	public ProcessDescriptor uploadProcessXml(String processXml,int version)throws InvalidModelException;
	
	/**
	 * 通过该方法更新process descriptor中的信息，相关字段的值如下：<br/>
	 * publishState:发布状态<br/>
	 * validDateFrom:流程有效期始<br/>
	 * validDateTo：流程有效期止<br/>
	 * ownerId:所有者id<br/>
	 * ownerName：所有者名称<br/>
	 * approver:审批人<br/>
	 * approvedTime:审批时间<br/>
	 * updateLog:发布日志<br/>
	 * 
	 * @param processDescriptor 流程定义描述符
	 */
	public void updateProcessDescriptor(ProcessDescriptor processDescriptor);
	
	

	/**
	 * 注意：Service repository废除了,2014-01-10
	 */
	/*
	public List<ServiceDescriptor> uploadServicesStream(InputStream inStream,Boolean publishState, Map<ServiceDescriptorProperty,Object> metadata)throws InvalidModelException;
	*/
	/**
	 * 注意：Resource repository废除了,2014-01-10
	 */
	/*
	public List<ResourceDescriptor> uploadResourcesStream(InputStream inStream,Boolean publishState, Map<ResourceDescriptorProperty,Object> metadata)throws InvalidModelException;
	*/
	/*****************************************************************/
	/***************   流程变量相关的api ******************************/
	/*****************************************************************/
	
	/**
	 * TODO XML数据用什么对象表达呢？
	 * 远程接口中，复杂的Object对象被Xtream转换成xml返回
	 */
	public Object getVariableValue(Scope scope,String name);
	/**
	 * TODO Xml数据用什么对象表达呢~~~
	 * @param scope
	 * @param name
	 * @param value
	 * @throws InvalidOperationException
	 */
	public void setVariableValue(Scope scope,String name,Object value)throws InvalidOperationException;
	
	/**
	 * TODO 该函数是否有必要？
	 * @param scope
	 * @param name
	 * @param value
	 * @param headers
	 * @throws InvalidOperationException
	 */
	public void setVariableValue(Scope scope,String name,Object value,Properties headers)throws InvalidOperationException;
	public Map<String ,Object> getVariableValues(Scope scope);
	
	//TODO 增加一个saveOrUpdateVariable(Variable var)吗？
	
	/*****************************************************************/
	/***************   查询相关的api ******************************/
	/*****************************************************************/

	/**
	 * 通过ProcessKey从数据库中查询流程定义对象，
	 */
	public Object getWorkflowProcess(ProcessKey key) throws InvalidModelException;

	public String getWorkflowProcessXml(ProcessKey key);
	
	/**
	 * 通过ActivityInstance或者ProcessInstance查询对应的流程定义对象；
	 * 例如输入ActivityInstance返回与之对应的Activity；
	 * 输入ProcessInstance返回与之对应的SubProcess；
	 * 其他Scope类型参数均为非法，返回null
	 * @param scope ActivityInstance或者ProcessInstance
	 * @return 与scope对应的流程定义对象。
	 * @throws InvalidModelException
	 */
	public Object getWorkflowDefinitionElement(Scope scope)throws InvalidModelException;
}
