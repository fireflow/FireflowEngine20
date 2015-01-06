package org.fireflow.demo.workflow.ext;

import org.fireflow.engine.modules.ousystem.impl.UserImpl;

public class WorkflowUser extends UserImpl {
	private String groupCode = null;
	
	private String groupName = null;
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}
