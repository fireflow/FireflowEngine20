package org.fireflow.demo.workflow.ext;

import org.fireflow.engine.entity.runtime.impl.AbsProcessInstance;

public class ProcessInstanceEx extends AbsProcessInstance {
	int stateValue = 0;
	String stateDisplayName = "";
	
	/**
	 * 当前环节实例
	 */
	String currentActivityInstances = null;

	public int getStateValue() {
		return stateValue;
	}

	public void setStateValue(int stateValue) {
		this.stateValue = stateValue;
	}

	public String getStateDisplayName() {
		return stateDisplayName;
	}

	public void setStateDisplayName(String stateDisplayName) {
		this.stateDisplayName = stateDisplayName;
	}

	public String getCurrentActivityInstances() {
		return currentActivityInstances;
	}

	public void setCurrentActivityInstances(String currentActivityInstances) {
		this.currentActivityInstances = currentActivityInstances;
	}
	
	
}
