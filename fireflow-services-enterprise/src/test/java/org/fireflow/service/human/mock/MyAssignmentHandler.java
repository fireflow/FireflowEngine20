package org.fireflow.service.human.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.invocation.impl.AbsAssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

public class MyAssignmentHandler extends AbsAssignmentHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8611837283286947711L;

	@Override
	public List<User> resolvePotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		List<User> list = new ArrayList<User>();
		UserImpl u = new UserImpl();
		u.setName("张三");
		u.setId("zhangsan");
		u.setDeptId("Research Dept.");
		u.setDeptName("研发部");
		
		list.add(u);
		
		u = new UserImpl();
		u.setId("lisi");
		u.setName("李四");
		u.setDeptId("Research Dept.");
		u.setDeptName("研发部");
		list.add(u);
		
		return list;
	}

	@Override
	public List<User> resolveReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		List<User> list = new ArrayList<User>();
		UserImpl u = new UserImpl();
		u.setName("王总");
		u.setId("Mgr-W");
		u.setDeptId("Research Dept.");
		u.setDeptName("研发部");
		
		list.add(u);
		
		u = new UserImpl();
		u.setId("Mgr_C");
		u.setName("陈总");
		u.setDeptId("Research Dept.");
		u.setDeptName("研发部");
		list.add(u);
		
		return list;
	}

	@Override
	public List<User> resolveAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkItemAssignmentStrategy resolveAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.service.human.impl.AbsAssignmentHandler#resolveWorkItemPropertyValues()
	 */
	@Override
	public Map<WorkItemProperty, Object> resolveWorkItemPropertyValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
