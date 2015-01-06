package org.fireflow.demo.workflow.ext;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.demo.workflow.Constants;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 解析申请者所在的部门的部门经理。
 * 很多情况下，流程申请者和录入者不是同一人；需要根据申请者的部门找到部门经理。
 * 由于在流程引擎T_ff_rt_ProcessInstance表中仅存储了流程的创建者（录入人）信息，因此需要将申请设的departmentid保存到流程变量中
 * @author apple
 *
 */
public class ApplicantDeptMgrResolver extends ResourceResolver {

	@Override
	public List<User> resolve(WorkflowSession session, ProcessInstance procInst,
			ActivityInstance actInst, ResourceDef resourceRef) {
		//首先检查流程变量中有无APPLICANT_GROUP_CODE，如果没有就采用ProcessInstance.creatorDepartmentId
		//作为当前部门
		String groupCode = procInst.getCreatorDeptId();
		
		WorkflowStatement statement = session.createWorkflowStatement();
		Object obj = statement.getVariableValue(procInst, Constants.VAR_NAME_APPLICANT_GROUP_CODE);
		
		if (obj!=null && obj instanceof String){
			String tmp = (String)obj;
			if (!tmp.trim().equals("")){
				groupCode = tmp;//如果流程变量中存储有申请人的部门id,则优先使用
			}
		}
		
		//资源代号存储在 resourceRef.value字段中
		String roleCode = resourceRef.getValue();
		
		//通过OUSystemConnector找出所有的候选用户
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		RuntimeContext rtc = localSession.getRuntimeContext();
		
		OUSystemConnector ouConnector = rtc.getEngineModule(OUSystemConnector.class, procInst.getProcessType());
		
		List<User> candidatesList = ouConnector.findUsersInRole(roleCode);//
		
		//根据groupCode筛选出符合条件的部门经理
		List<User> result = new ArrayList<User>();
		
		if (candidatesList!=null){
			for (User u : candidatesList){
				if (groupCode.equals(((WorkflowUser)u).getGroupCode())){
					result.add(u);
				}
			}
		}
		
		
		return result;
	}

}
