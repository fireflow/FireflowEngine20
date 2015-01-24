package org.fireflow.demo.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.workflow.ext.ProcessInstanceEx;
import org.fireflow.demo.workflow.ext.WorkItemExt;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.service.java.JavaService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

@At("/module/workflow/WorkflowModule")
@IocBean(fields={"fireflowRuntimeContext","dao"})
public class WorkflowModule {
    private static final Log log = Logs.get();
    
    private Dao dao;

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dao dao() {
        return dao;
    }
    
	private RuntimeContext fireflowRuntimeContext = null;
	
	

	public RuntimeContext getFireflowRuntimeContext() {
		return fireflowRuntimeContext;
	}

	public void setFireflowRuntimeContext(RuntimeContext fireflowRuntimeContext) {
		this.fireflowRuntimeContext = fireflowRuntimeContext;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> claimWorkItemJson(final @Param("workItemId")String workItemId){
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		final WorkflowStatement statement = fireSession.createWorkflowStatement();

		Map<String,Object> result = new HashMap<String,Object>();
		try {
			WorkItem wi = statement.claimWorkItem(workItemId);
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_RECORD_KEY, wi);
		} catch (InvalidOperationException e) {
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, e.getMessage());
		}
		return result;
	}
	
	@At
	@Ok("jsp:/template/workflow/_select_next_step_actors.jsp")
	@Fail("jsp:/template/include/_error.jsp")
	public Map<String,Object> showNextActorCandidates(@Param("workItemId") String workItemId)throws Exception{
		final org.fireflow.engine.modules.ousystem.User u =
				WorkflowUtil.getCurrentWorkflowUser();
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireflowRuntimeContext, u);

		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem wi = workItemQuery.get(workItemId);

		if (wi==null){
			Exception e = new Exception("没有找到工作项，workItemId=[" + workItemId
					+ "]。");
			throw e;
		}
		
		if (!(wi instanceof LocalWorkItemImpl)){
			Exception e = new Exception("仅可以对Local WorkItem执行该操作，workItemId=[" + workItemId
					+ "]，当前workitem类型是"+wi.getClass().getName());
			throw e;

		}

		LocalWorkItem workItem = (LocalWorkItem)wi;
		
		//1、找到当前Activity的后继Activity

		Activity activity = (Activity)WorkflowUtil.getThisActivity(workflowSession, workItem);
		
		if (activity==null){
			Exception e = new Exception("流程[processId="+workItem.getProcessId()+"]中没有id为"+workItem.getActivityId()+"的Activity。");
			throw e;			
		}

		
		//
		List<Activity> nextActivities = activity.getNextActivities();
		
		if(nextActivities != null && nextActivities.size() > 0){
			//这种for(Activity active:nextActivities)写法，下面nextActivities.remove(active)会报java.util.ConcurrentModificationException
			Iterator<Activity> iterator = nextActivities.iterator();  
		     while(iterator.hasNext()) {  
		    	 Activity active = iterator.next();  
		         ServiceBinding sb =  active.getServiceBinding();
					if(sb != null ){
						String serviceId = sb.getServiceId();
						if(StringUtils.isNotBlank(serviceId)){
							SubProcess subProcess = (SubProcess)active.getParent();
							if (subProcess!=null){
								WorkflowProcess wp = (WorkflowProcess)subProcess.getParent();
								if (wp!=null){
									ServiceDef sd = wp.getService(serviceId);
									
									if (sd!=null && sd instanceof JavaService){
										//标识为java节点，不需要指定下一个审批人。
										//nextActivities.remove(active);  
										iterator.remove();  
									}
								}
							}
						}
					}  
		     }  
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		
		result.put("nextActivities", nextActivities);
		result.put("thisActivity", activity);
		result.put("workItemId", workItemId);
		
	
		
		//3、跳转到表单页面，让用户补充业务信息
		return result;
	}
	
	
	@At
	@Ok("jsp:/template/workflow/CompleteWorkItemOk.jsp")
	public Map<String,String> completeWorkItem(final @Param("workItemId")String workItemId){
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		final WorkflowStatement statement = fireSession.createWorkflowStatement();
		final WorkflowQuery<WorkItem> wiQ = fireSession.createWorkflowQuery(WorkItem.class);
		final Map<String ,String> nextInfo = new HashMap<String,String>();
		Trans.exec(new Atom() {
			public void run() {
				try {
					WorkItem wi = statement.completeWorkItem(workItemId, null, null, null);

					WorkflowQuery<ActivityInstance> actInstQ = fireSession.createWorkflowQuery(ActivityInstance.class);
					actInstQ.add(Restrictions.gt(ActivityInstanceProperty.STEP_NUMBER, ((LocalWorkItemImpl)wi).getStepNumber()))
							.add(Restrictions.lt(ActivityInstanceProperty.STATE, ActivityInstanceState.DELIMITER))
							.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, ((LocalWorkItem)wi).getProcessInstanceId()));
					//List<Activity> nextActList = WorkflowUtil.getNextActivities(fireSession, (LocalWorkItem)wi);
					List<ActivityInstance> nextActList= actInstQ.list();
					
					if (nextActList!=null){
						
						Map<String,String> actIds = new HashMap<String,String>();
						for (ActivityInstance act : nextActList){
							actIds.put(act.getNodeId(),act.getDisplayName());
							
						}
						if (actIds.isEmpty()){
							nextInfo.put("流程结束", "");
						}else{
							wiQ.reset();
							wiQ.add(Restrictions.in(WorkItemProperty.ACTIVITY_ID, actIds.keySet().toArray()));
							wiQ.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID,((LocalWorkItem)wi).getProcessInstanceId()))
								.add(Restrictions.ne(WorkItemProperty.STATE, WorkItemState.COMPLETED));
							List<WorkItem> wiList = wiQ.list();
							
							if (wiList!=null){
								for (WorkItem lwi:wiList){
									String actId = ((LocalWorkItem)lwi).getActivityId();
									String actDispName = actIds.get(actId);
									String ownerName = ((LocalWorkItem)lwi).getOwnerName();
									
									String ownerNameList = nextInfo.get(actDispName);
									if (ownerNameList==null){
										nextInfo.put(actDispName, ownerName);
									}else{
										if(!ownerNameList.equals(ownerName)){
											ownerNameList = ownerNameList+", "+ownerName;
											nextInfo.put(actDispName, ownerNameList);
										}
									}
								}
							}
						}


					}
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					log.error(Utils.exceptionStackToString(e));
					e.printStackTrace();
				}
			}
		});

		return nextInfo;
	}
	
	@At
	@Ok("jsp:/template/workflow/CompleteWorkItemOk.jsp")
	public Map<String,String> completeWorkItemWithNextActors(HttpServletRequest req){
		final String workItemId = req.getParameter("workItemId");
		String[] activityIdArr = req.getParameterValues("activityId");
		
		final Map<String,AssignmentHandler> assignmentHandlersMap = new HashMap<String,AssignmentHandler>();
		if (activityIdArr!=null){
			for (String actId : activityIdArr){
				String[] userIdArr = req.getParameterValues(actId);
				if (userIdArr!=null && userIdArr.length>0){
					List<User> userList = dao().query(User.class, Cnd.where("loginName","in",userIdArr));
					
					if (userList!=null){
						List<org.fireflow.engine.modules.ousystem.User> fireUserList = new ArrayList<org.fireflow.engine.modules.ousystem.User>();
						for (User u : userList){
							org.fireflow.engine.modules.ousystem.User fireUser = WorkflowUtil.convertAppUser2FireflowUser(u);
							fireUserList.add(fireUser);
						}
						
						DynamicAssignmentHandler hand = new DynamicAssignmentHandler();
						
						hand.setPotentialOwners(fireUserList);
						
						assignmentHandlersMap.put(actId, hand);
					}
				}
				
				
			}
		}
		
		
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		final WorkflowStatement statement = fireSession.createWorkflowStatement();
		final WorkflowQuery<WorkItem> wiQ = fireSession.createWorkflowQuery(WorkItem.class);
		final Map<String ,String> nextInfo = new HashMap<String,String>();
		Trans.exec(new Atom() {
			public void run() {
				try {
					WorkItem wi = statement.completeWorkItem(workItemId, assignmentHandlersMap, null,null, null);
					
					
					List<Activity> nextActList = WorkflowUtil.getNextActivities(fireSession, (LocalWorkItem)wi);
					
					if (nextActList!=null){
						
						Map<String,String> actIds = new HashMap<String,String>();
						for (Activity act : nextActList){
							actIds.put(act.getId(),act.getDisplayName());
							
						}
						wiQ.reset();
						wiQ.add(Restrictions.in(WorkItemProperty.ACTIVITY_ID, actIds.keySet().toArray()))
							.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID,((LocalWorkItem)wi).getProcessInstanceId()));
						List<WorkItem> wiList = wiQ.list();
						
						if (wiList!=null){
							for (WorkItem lwi:wiList){
								String actId = ((LocalWorkItem)lwi).getActivityId();
								String actDispName = actIds.get(actId);
								String ownerName = ((LocalWorkItem)lwi).getOwnerName();
								
								String ownerNameList = nextInfo.get(actDispName);
								if (ownerNameList==null){
									nextInfo.put(actDispName, ownerName);
								}else{
									ownerNameList = ownerNameList+", "+ownerName;
									nextInfo.put(actDispName, ownerNameList);
								}
								
							}
						}

					}
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					log.error(Utils.exceptionStackToString(e));
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					log.error(Utils.exceptionStackToString(e));
				}
			}
		});

		return nextInfo;
	}
	
	
	@At
	@Ok("Json")
	public Map<String, Object> showHistoryJson(
			@Param("workItemId") String workItemId) {
		Map<String,Object> result = new HashMap<String,Object>();
		if (workItemId==null || workItemId.trim().equals("")){
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "输入的工作项Id不能为空");
			return result;
		}

		
		final org.fireflow.engine.modules.ousystem.User currentUser = WorkflowUtil
				.getCurrentWorkflowUser();

		final WorkflowSession fireSession = WorkflowSessionFactory
				.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<WorkItem> wiQ = fireSession
				.createWorkflowQuery(WorkItem.class);

		wiQ.reset();
		wiQ.add(Restrictions.eq(WorkItemProperty.ID, workItemId));

		LocalWorkItem currentWi = (LocalWorkItem)wiQ.unique();
		
		if (currentWi==null){
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "系统中没有id="+workItemId+"的工作项");
			return result;
		}
		
		
		wiQ.reset();
		wiQ.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, currentWi.getProcessInstanceId()))
			.addOrder(Order.desc(WorkItemProperty.STEP_NUMBER));
		
		List<WorkItem> historyWorkItems = wiQ.list();
		
		List<WorkItemExt> list2 = WorkflowUtil.workItemListToWorkItemExtList(historyWorkItems);

		int count = wiQ.count();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;

	}
	
	@At
	@Ok("jsp:/template/workflow/TodoList.jsp")
	public Map<String,Object> gotoTodoList(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
				WorkflowUtil.getCurrentWorkflowUser();
			
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<ProcessDescriptor> query = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		
		query.addOrder(Order.asc(ProcessDescriptorProperty.DISPLAY_NAME));
		
		List<ProcessDescriptor> descriptors = query.list();
		List<ProcessDescriptor> processDescriptorsList = new ArrayList<ProcessDescriptor>();
		
		Map<String,ProcessDescriptor> exists = new HashMap<String,ProcessDescriptor>();
		
		for (ProcessDescriptor descriptor : descriptors){
			String id = descriptor.getProcessId();
			if (exists.containsKey(id)){
				continue;
			}
			
			
			processDescriptorsList.add(descriptor);
			exists.put(descriptor.getProcessId(), descriptor);
		}

		result.put("processDescriptors", processDescriptorsList);
		
		return result;
	}
	
	@At
	@Ok("jsp:/template/workflow/MyProcInstList.jsp")
	public Map<String,Object> gotoMyProcInst(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
				WorkflowUtil.getCurrentWorkflowUser();
			
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<ProcessDescriptor> query = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		
		query.addOrder(Order.asc(ProcessDescriptorProperty.DISPLAY_NAME));
		
		List<ProcessDescriptor> descriptors = query.list();
		List<ProcessDescriptor> processDescriptorsList = new ArrayList<ProcessDescriptor>();
		
		Map<String,ProcessDescriptor> exists = new HashMap<String,ProcessDescriptor>();
		
		for (ProcessDescriptor descriptor : descriptors){
			String id = descriptor.getProcessId();
			if (exists.containsKey(id)){
				continue;
			}
			
			
			processDescriptorsList.add(descriptor);
			exists.put(descriptor.getProcessId(), descriptor);
		}

		result.put("processDescriptors", processDescriptorsList);
		
		return result;
	}
	
	@At
	@Ok("jsp:/template/workflow/HaveDoneList.jsp")
	public Map<String,Object> gotoHaveDoneList(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
				WorkflowUtil.getCurrentWorkflowUser();
			
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<ProcessDescriptor> query = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		
		query.addOrder(Order.asc(ProcessDescriptorProperty.DISPLAY_NAME));
		
		List<ProcessDescriptor> descriptors = query.list();
		List<ProcessDescriptor> processDescriptorsList = new ArrayList<ProcessDescriptor>();
		
		Map<String,ProcessDescriptor> exists = new HashMap<String,ProcessDescriptor>();
		
		for (ProcessDescriptor descriptor : descriptors){
			String id = descriptor.getProcessId();
			if (exists.containsKey(id)){
				continue;
			}
			
			
			processDescriptorsList.add(descriptor);
			exists.put(descriptor.getProcessId(), descriptor);
		}

		result.put("processDescriptors", processDescriptorsList);
		
		return result;
	}	
	
	/**
	 * 查询开票登记环节的待办工作项
	 * @param jtStartIndex
	 * @param jtPageSize
	 * @param jtSorting
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> loadTodoWorkItems4FinancialCheck(@Param("jtStartIndex")int jtStartIndex,
			@Param("jtPageSize") int jtPageSize,@Param("jtSorting") String jtSorting){
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<WorkItem> wiQ = fireSession.createWorkflowQuery(WorkItem.class);
		wiQ.add(Restrictions.lt(WorkItemProperty.STATE, WorkItemState.DELIMITER));
		int pageNum = jtStartIndex/jtPageSize+1;
		wiQ.setPageNumber(pageNum);
		wiQ.setPageSize(jtPageSize);
		
		wiQ.add(Restrictions.lt(WorkItemProperty.STATE, WorkItemState.DELIMITER));
		wiQ.add(Restrictions.eq(WorkItemProperty.OWNER_ID, currentUser.getId()));
		wiQ.add(Restrictions.eq(WorkItemProperty.PROCESSS_ID, "InvoiceRequestProcess"));
		//开发票登记   
		wiQ.add(Restrictions.eq(WorkItemProperty.ACTIVITY_ID,"InvoiceRequestProcess.main.IssueInvoice"));//TODO 该条件需改进，不能用中文名做条件，要用workitem的name做条件
		if (jtSorting!=null && !jtSorting.trim().equals("") && !jtSorting.equalsIgnoreCase("undefined")){
			StringTokenizer tokenizer = new StringTokenizer(jtSorting,",");
			while (tokenizer.hasMoreTokens()){
				String ord = tokenizer.nextToken();
				int idx = ord.indexOf(" ");
				if (idx>0){
					String propNm = ord.substring(0,idx);
					if ("stateDisplayName".equals(propNm)){
						propNm = "state";
					}
					String orderType = ord.substring(idx+1);
					WorkItemProperty prop = WorkItemProperty.fromValue(propNm);
					if ("ASC".equalsIgnoreCase(orderType)){
						wiQ.addOrder(Order.asc(prop));
					}else{
						wiQ.addOrder(Order.desc(prop));
					}
				}
			}
		}else{
			wiQ.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		}
		
		List<WorkItem> list = wiQ.list();
		
		List<WorkItemExt> list2 = WorkflowUtil.workItemListToWorkItemExtList(list);

		int count = wiQ.count();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> listMyProcInstJson(@Param("jtStartIndex")int jtStartIndex,
			@Param("jtPageSize") int jtPageSize,@Param("jtSorting") String jtSorting,
			@Param("processId") String processId){
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<ProcessInstance> wiQ = fireSession.createWorkflowQuery(ProcessInstance.class);
		wiQ.add(Restrictions.eq(ProcessInstanceProperty.CREATOR_ID, currentUser.getId()));
		int pageNum = jtStartIndex/jtPageSize+1;
		wiQ.setPageNumber(pageNum);
		wiQ.setPageSize(jtPageSize);
		
		if (processId!=null && !"--".equals(processId)){
			wiQ.add(Restrictions.eq(ProcessInstanceProperty.PROCESS_ID, processId));
		}
		if (jtSorting!=null && !jtSorting.trim().equals("") && !jtSorting.equalsIgnoreCase("undefined")){
			StringTokenizer tokenizer = new StringTokenizer(jtSorting,",");
			while (tokenizer.hasMoreTokens()){
				String ord = tokenizer.nextToken();
				int idx = ord.indexOf(" ");
				if (idx>0){
					String propNm = ord.substring(0,idx);
					if ("stateDisplayName".equals(propNm)){
						propNm = "state";
					}
					String orderType = ord.substring(idx+1);
					ProcessInstanceProperty prop = ProcessInstanceProperty.fromValue(propNm);
					if ("ASC".equalsIgnoreCase(orderType)){
						wiQ.addOrder(Order.asc(prop));
					}else{
						wiQ.addOrder(Order.desc(prop));
					}
				}
			}
		}else{
			wiQ.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		}
		
		List<ProcessInstance> list = wiQ.list();
		
		Map<String,String> currentActInst = new HashMap<String,String>();
		
		//查询activityInstance
		if (list!=null && list.size()>0){
			List<String> procInstIdList = new ArrayList<String>();
			
			for (ProcessInstance procInst : list){
				procInstIdList.add(procInst.getId());
			}
			
			final WorkflowQuery<ActivityInstance> actQ = fireSession.createWorkflowQuery(ActivityInstance.class);
			actQ.add(Restrictions.in(ActivityInstanceProperty.PROCESS_INSTANCE_ID, procInstIdList.toArray()))
				.add(Restrictions.lt(ActivityInstanceProperty.STATE, ActivityInstanceState.DELIMITER));
			
			List<ActivityInstance> actInstList = actQ.list();
			if (actInstList!=null && actInstList.size()>0){
				for (ActivityInstance actInst : actInstList){
					if (actInst.getServiceId()==null || actInst.getServiceId().trim().equals("")){
						continue;
					}
					String s = currentActInst.get(actInst.getProcessInstanceId());
					if (s!=null && !s.trim().equals("")){
						s = s+"；"+actInst.getDisplayName();
						currentActInst.put(actInst.getProcessInstanceId(),s);
					}else{
						currentActInst.put(actInst.getProcessInstanceId(),actInst.getDisplayName());
					}
				}
			}
			
		}

		
		
		List<ProcessInstanceEx> list2 = WorkflowUtil.processInstanceListToProcessInstanceExtList(list,currentActInst);

		int count = wiQ.count();
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("currentProcessId", processId);
		
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;

	}
	
	
	
	/**
	 * 查询待办任务
	 * @param jtStartIndex
	 * @param jtPageSize
	 * @param jtSorting
	 * @param processId
	 * @param status
	 * @param workItemName
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> listHaveDoneWorkItemsJson(@Param("jtStartIndex")int jtStartIndex,
			@Param("jtPageSize") int jtPageSize,@Param("jtSorting") String jtSorting,
			@Param("processId") String processId){
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<WorkItem> wiQ = fireSession.createWorkflowQuery(WorkItem.class);
		wiQ.add(Restrictions.eq(WorkItemProperty.OWNER_ID, currentUser.getId()));
		wiQ.add(Restrictions.gt(WorkItemProperty.STATE, WorkItemState.DELIMITER));

		int pageNum = jtStartIndex/jtPageSize+1;
		wiQ.setPageNumber(pageNum);
		wiQ.setPageSize(jtPageSize);


		if (processId!=null && !"--".equals(processId)){
			wiQ.add(Restrictions.eq(WorkItemProperty.PROCESSS_ID, processId));
		}
		
		if (jtSorting!=null && !jtSorting.trim().equals("") && !jtSorting.equalsIgnoreCase("undefined")){
			StringTokenizer tokenizer = new StringTokenizer(jtSorting,",");
			while (tokenizer.hasMoreTokens()){
				String ord = tokenizer.nextToken();
				int idx = ord.indexOf(" ");
				if (idx>0){
					String propNm = ord.substring(0,idx);
					if ("stateDisplayName".equals(propNm)){
						propNm = "state";
					}
					String orderType = ord.substring(idx+1);
					WorkItemProperty prop = WorkItemProperty.fromValue(propNm);
					if ("ASC".equalsIgnoreCase(orderType)){
						wiQ.addOrder(Order.asc(prop));
					}else{
						wiQ.addOrder(Order.desc(prop));
					}
				}
			}
		}else{
			wiQ.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		}
		
		List<WorkItem> list = wiQ.list();
		
		List<WorkItemExt> list2 = WorkflowUtil.workItemListToWorkItemExtList(list);

		int count = wiQ.count();
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("currentProcessId", processId);
		
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;
	}	
	
	/**
	 * 查询待办任务
	 * @param jtStartIndex
	 * @param jtPageSize
	 * @param jtSorting
	 * @param processId
	 * @param status
	 * @param workItemName
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> listTodoWorkItemsJson(@Param("jtStartIndex")int jtStartIndex,
			@Param("jtPageSize") int jtPageSize,@Param("jtSorting") String jtSorting,
			@Param("processId") String processId,@Param("state") String state){
		
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<WorkItem> wiQ = fireSession.createWorkflowQuery(WorkItem.class);
		wiQ.add(Restrictions.eq(WorkItemProperty.OWNER_ID, currentUser.getId()));
		wiQ.add(Restrictions.lt(WorkItemProperty.STATE, WorkItemState.DELIMITER));
		
		int pageNum = jtStartIndex/jtPageSize+1;
		wiQ.setPageNumber(pageNum);
		wiQ.setPageSize(jtPageSize);
		
		//查询条件
		if (state!=null && !"--".equals(state)){
			wiQ.add(Restrictions.eq(WorkItemProperty.STATE, WorkItemState.fromValue(Integer.parseInt(state))));
			
		}

		if (processId!=null && !"--".equals(processId)){
			wiQ.add(Restrictions.eq(WorkItemProperty.PROCESSS_ID, processId));
		}
		
		if (jtSorting!=null && !jtSorting.trim().equals("") && !jtSorting.equalsIgnoreCase("undefined")){
			StringTokenizer tokenizer = new StringTokenizer(jtSorting,",");
			while (tokenizer.hasMoreTokens()){
				String ord = tokenizer.nextToken();
				int idx = ord.indexOf(" ");
				if (idx>0){
					String propNm = ord.substring(0,idx);
					if ("stateDisplayName".equals(propNm)){
						propNm = "state";
					}
					String orderType = ord.substring(idx+1);
					WorkItemProperty prop = WorkItemProperty.fromValue(propNm);
					if ("ASC".equalsIgnoreCase(orderType)){
						wiQ.addOrder(Order.asc(prop));
					}else{
						wiQ.addOrder(Order.desc(prop));
					}
				}
			}
		}else{
			wiQ.addOrder(Order.desc(WorkItemProperty.CREATED_TIME));
		}
		
		List<WorkItem> list = wiQ.list();
		
		List<WorkItemExt> list2 = WorkflowUtil.workItemListToWorkItemExtList(list);

		int count = wiQ.count();
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("currentProcessId", processId);
		result.put("currentState", state);
		
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;
	}
	
	/**
	 * 中止流程实例
	 * @param processInstanceId
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> abortProcessInstance(@Param("processInstanceId") String processInstanceId){
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();
		final WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		final WorkflowStatement statement = fireSession.createWorkflowStatement();

		Map<String,Object> result = new HashMap<String,Object>();
		try {
			ProcessInstance wi = statement.abortProcessInstance(processInstanceId, "");
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_RECORD_KEY, wi);
		} catch (InvalidOperationException e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, e.getMessage());
		}
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> showHistoryByProcessInstanceIdJson(@Param("processInstanceId") String processInstanceId){
		Map<String,Object> result = new HashMap<String,Object>();

		
		final org.fireflow.engine.modules.ousystem.User currentUser = WorkflowUtil
				.getCurrentWorkflowUser();

		final WorkflowSession fireSession = WorkflowSessionFactory
				.createWorkflowSession(fireflowRuntimeContext, currentUser);

		final WorkflowQuery<WorkItem> wiQ = fireSession
				.createWorkflowQuery(WorkItem.class);

		
		
		wiQ.reset();
		wiQ.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, processInstanceId))
			.addOrder(Order.desc(WorkItemProperty.STEP_NUMBER));
		
		List<WorkItem> historyWorkItems = wiQ.list();
		
		List<WorkItemExt> list2 = WorkflowUtil.workItemListToWorkItemExtList(historyWorkItems);

		int count = wiQ.count();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list2);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, count);
		return result;

	}
}
